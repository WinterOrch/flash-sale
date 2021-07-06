package com.winter.flashsale.feign;

import com.winter.common.model.ApiResponse;

public interface CouponFeign {

    ApiResponse getLatestFlashSaleSession();
}
