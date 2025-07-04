package com.web.appleshop.dto.request;

import com.web.appleshop.enums.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserOrderSearchCriteria extends BaseOrderSearchCriteria{
    @Override
    public boolean isUserSpecific() {
        return true;
    }
}
