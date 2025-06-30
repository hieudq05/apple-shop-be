package com.web.appleshop.service;

import com.web.appleshop.config.VnPayConfig;
import com.web.appleshop.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayService {
    private final VnPayConfig vnPayConfig;

    public String createPaymentUrl(HttpServletRequest request, long amount, String orderInfo) {
        Map<String, String> vnp_Params = getVnpayParam(request, amount, orderInfo);
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet().stream().sorted().toList());
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_secureHash = VnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_secureHash;
        return vnPayConfig.getUrl() + "?" + queryUrl;
    }

    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();

        // 1. Lấy các tham số trả về từ VNPAY
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        // 2. Tách chữ ký ra khỏi dữ liệu tham số
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            throw new BadRequestException("Lỗi khi xác thực thanh toán.");
        }

        // 3. Tái tạo lại dữ liệu
        // Sắp xếp theo thứ tự Alphabet
        List<String> fieldNames = new ArrayList<>(fields.keySet().stream().sorted().toList());

        // Tạo chuỗi ký tự để tạo chữ ký
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)).append('&');
            }
        }

        if (!hashData.isEmpty()) {
            hashData.setLength(hashData.length() - 1);
        }

        // 4. Tao chữ ký
        String vnp_SecureHashNew = VnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());

        // 5. So sánh chữ ký
        if (vnp_SecureHash.equals(vnp_SecureHashNew)) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    private Map<String, String> getVnpayParam(HttpServletRequest request, long amount, String orderInfo) {
        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        long vnp_Amount = amount * 100;
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);
        String vnp_OrderType = vnPayConfig.getOrderType();
        String vnp_Locale = "vn";
        String vnp_ReturnUrl = vnPayConfig.getReturnUrl();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = sdf.format(calendar.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = sdf.format(calendar.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnp_Params;
    }
}
