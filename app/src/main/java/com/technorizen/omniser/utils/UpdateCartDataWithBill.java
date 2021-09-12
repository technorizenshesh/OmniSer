package com.technorizen.omniser.utils;

import com.technorizen.omniser.fooddelivery.models.ModelResTypeItems;

import java.util.ArrayList;

public interface UpdateCartDataWithBill {

    void updateCartData(ArrayList<ModelResTypeItems.Result.Item_data> dataList, int itemPosition);

}
