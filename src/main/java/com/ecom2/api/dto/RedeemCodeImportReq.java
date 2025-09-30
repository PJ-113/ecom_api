package com.ecom2.api.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record RedeemCodeImportReq(
        @NotNull Long productId,
        @NotNull List<String> codes
) {}
