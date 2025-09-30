package com.ecom2.api.dto;

import java.util.List;

public record RedeemCodeImportReqDto(
		Long productId,
        List<String> codes
        ) {}
