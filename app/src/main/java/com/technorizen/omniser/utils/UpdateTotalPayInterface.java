package com.technorizen.omniser.utils;

import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;

import java.util.ArrayList;

public interface UpdateTotalPayInterface {

    void updateCartData(String itemTotal, String devCharge, String totalPlusDev);
    void getStoreId(String storeId);

}
