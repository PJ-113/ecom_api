package com.ecom2.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom2.api.dto.RedeemCodeImportReq;
import com.ecom2.entity.Product;
import com.ecom2.entity.RedeemCode;
import com.ecom2.service.ProductService;
import com.ecom2.service.RedeemCodeService;

@RestController
@RequestMapping("/api/admin/redeem-codes")
@CrossOrigin
public class RedeemCodeRestController {

    private final ProductService productService;
    private final RedeemCodeService redeemCodeService;

    public RedeemCodeRestController(ProductService productService, RedeemCodeService redeemCodeService) {
        this.productService = productService;
        this.redeemCodeService = redeemCodeService;
    }

    @GetMapping("/stock/{productId}")
    public long stock(@PathVariable Long productId) {
        Product p = productService.getAny(productId);
        return redeemCodeService.availableStock(p);
    }

    @GetMapping("/product/{productId}")
    public List<String> listCodes(@PathVariable Long productId) {
        Product p = productService.getAny(productId);
        return redeemCodeService.listAllCodes(p).stream().map(RedeemCode::getCode).toList();
    }


    
    @PostMapping(path = "/import/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public int importCodesJson(@PathVariable Long productId,
                               @RequestBody RedeemCodeImportReq req) {
        Product p = productService.getAny(productId);
        return redeemCodeService.importCodes(p, req.codes());
    }

   
    @PostMapping(path = "/import/{productId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public int importCodesText(@PathVariable Long productId,
                               @RequestBody String raw) {
        Product p = productService.getAny(productId);
        List<String> codes = raw.lines().map(String::trim)
                .filter(s -> !s.isBlank()).toList();
        return redeemCodeService.importCodes(p, codes);
    }
}
