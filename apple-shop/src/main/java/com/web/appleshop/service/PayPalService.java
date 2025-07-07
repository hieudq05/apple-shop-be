package com.web.appleshop.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.web.appleshop.dto.PaymentDto;
import com.web.appleshop.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayPalService {
    private static final Logger log = LoggerFactory.getLogger(PayPalService.class);
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    // Create a proper decimal formatter for PayPal
    private String formatAmount(Double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#0.00", symbols);
        return df.format(amount);
    }

    // Convert VND to USD (approximate rate for testing)
    private Double convertVndToUsd(Double vndAmount) {
        // Using approximate rate: 1 USD = 24,000 VND
        // You should use real exchange rate API in production
        return vndAmount / 24000.0;
    }

    public PaymentDto.PayPalResponse createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl,
            Integer orderId
    ) {
        try {
            Double totalUsd = convertVndToUsd(total);

            // Format amount properly
            String formattedAmount = formatAmount(totalUsd);

            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(formattedAmount);

            Transaction transaction = new Transaction();
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setCustom(orderId.toString()); // Store order ID

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(method);

            Payment payment = new Payment();
            payment.setIntent(intent);
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            payment.setRedirectUrls(redirectUrls);

            APIContext context = new APIContext(clientId, clientSecret, mode);
            Payment createdPayment = payment.create(context);

            String approvalUrl = createdPayment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst()
                    .map(Links::getHref)
                    .orElse("");

            return new PaymentDto.PayPalResponse(
                    "00",
                    "Payment created successfully",
                    approvalUrl,
                    createdPayment.getId()
            );

        } catch (PayPalRESTException e) {
            throw new PaymentException("Error creating PayPal payment: " + e.getMessage());
        }
    }

    public PaymentDto.PayPalExecuteResponse executePayment(String paymentId, String payerId) {
        try {
            APIContext context = new APIContext(clientId, clientSecret, mode);
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment executedPayment = payment.execute(context, paymentExecution);

            // Extract order ID from custom field
            String orderId = executedPayment.getTransactions().getFirst().getCustom();

            return new PaymentDto.PayPalExecuteResponse(
                    "00",
                    "Payment executed successfully",
                    executedPayment.getId(),
                    executedPayment.getState(),
                    Integer.parseInt(orderId)
            );

        } catch (PayPalRESTException e) {
            // Handle specific error for already completed payments
            if ("PAYMENT_ALREADY_DONE".equals(e.getDetails().getName())) {
                log.warn("Payment already completed, retrieving existing payment info: {}", paymentId);
                try {
                    APIContext context = new APIContext(clientId, clientSecret, mode);
                    Payment existingPayment = Payment.get(context, paymentId);
                    String orderId = existingPayment.getTransactions().get(0).getCustom();

                    return new PaymentDto.PayPalExecuteResponse(
                            "00",
                            "Payment already completed",
                            existingPayment.getId(),
                            existingPayment.getState(),
                            Integer.parseInt(orderId)
                    );
                } catch (Exception ex) {
                    throw new PaymentException("Error retrieving completed payment: " + ex.getMessage());
                }
            }

            log.error("PayPal execution error: {}", e.getDetails());
            throw new PaymentException("Error executing PayPal payment: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error executing PayPal payment: {}", e.getMessage());
            throw new PaymentException("Unexpected error: " + e.getMessage());
        }
    }
}
