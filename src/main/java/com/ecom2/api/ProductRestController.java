package com.ecom2.api;



import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecom2.api.dto.ProductDto;
import com.ecom2.api.dto.ProductUpsertReq;
import com.ecom2.api.mapper.ApiMapper;
import com.ecom2.entity.Product;
import com.ecom2.service.ProductService;
import com.ecom2.service.RedeemCodeService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductRestController {

 private final ProductService productService;
 private final RedeemCodeService redeemCodeService;

 // โฟลเดอร์เก็บรูป (กำหนดผ่าน application.properties: app.upload.dir=uploads)
 @Value("${app.upload.dir:uploads}")
 private String uploadDir;

 public ProductRestController(ProductService productService, RedeemCodeService redeemCodeService){
     this.productService = productService;
     this.redeemCodeService = redeemCodeService;
 }

 // ---------- ลูกค้า: สินค้าที่เปิดขาย ----------
 @GetMapping
 public List<ProductDto> all(){
     return productService.all().stream()
             .map(p -> ApiMapper.toDto(p, redeemCodeService.availableStock(p)))
             .toList();
 }

 @GetMapping("/{id}")
 public ProductDto get(@PathVariable Long id){
     Product p = productService.get(id);
     return ApiMapper.toDto(p, redeemCodeService.availableStock(p));
 }

 // ---------- แอดมิน: LIST ---------- (เหมือนกับ List Product)
 @GetMapping("/admin/all")
 public List<ProductDto> adminAll(){
     return productService.allForAdmin().stream()
             .map(p -> ApiMapper.toDto(p, redeemCodeService.availableStock(p)))
             .toList();
 }

 // ---------- แอดมิน: CREATE (JSON แบบเดิม) ----------
 @PostMapping(path = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
 public ProductDto createJson(@RequestBody @Valid ProductUpsertReq in){
     Product p = new Product();
     ApiMapper.apply(p, in);
     p = productService.save(p);
     return ApiMapper.toDto(p, redeemCodeService.availableStock(p));
 }

 // ---------- แอดมิน: CREATE (MULTIPART: ฟอร์ม + ไฟล์) ----------
 @PostMapping(path = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ProductDto createMultipart(
         @RequestParam String name,
         @RequestParam BigDecimal price,
         @RequestParam(required = false) String description,
         @RequestParam(defaultValue = "true") boolean active,
         @RequestParam(required = false) String imagePath,               // เผื่อไม่ได้อัปไฟล์แต่ส่ง path เดิม
         @RequestPart(value = "file", required = false) MultipartFile file
 ) throws IOException {
     Product p = new Product();
     p.setName(name);
     p.setPrice(price);
     p.setDescription(description);
     p.setActive(active);

     // ถ้ามีไฟล์ -> เซฟ แล้วตั้ง imagePath จากไฟล์ มิฉะนั้นใช้ imagePath ที่ส่งมา
     if (file != null && !file.isEmpty()) {
         p.setImagePath(storeFileAndGetWebPath(file));
     } else if (imagePath != null && !imagePath.isBlank()) {
         p.setImagePath(imagePath);
     }
     p = productService.save(p);
     return ApiMapper.toDto(p, redeemCodeService.availableStock(p));
 }

 // ---------- แอดมิน: UPDATE (JSON แบบเดิม) ----------
 @PutMapping(path = "/admin/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
 public ProductDto updateJson(@PathVariable Long id, @RequestBody @Valid ProductUpsertReq in){
     Product p = productService.getAny(id);
     ApiMapper.apply(p, in);
     p = productService.save(p);
     return ApiMapper.toDto(p, redeemCodeService.availableStock(p));
 }

 // ---------- แอดมิน: UPDATE (MULTIPART: ฟอร์ม + ไฟล์) ----------
 @PutMapping(path = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ProductDto updateMultipart(
         @PathVariable Long id,
         @RequestParam(required = false) String name,
         @RequestParam(required = false) BigDecimal price,
         @RequestParam(required = false) String description,
         @RequestParam(required = false) Boolean active,
         @RequestParam(required = false) String imagePath,
         @RequestPart(value = "file", required = false) MultipartFile file
 ) throws IOException {
     Product p = productService.getAny(id);
     if (name != null) p.setName(name);
     if (price != null) p.setPrice(price);
     if (description != null) p.setDescription(description);
     if (active != null) p.setActive(active);

     if (file != null && !file.isEmpty()) {
         p.setImagePath(storeFileAndGetWebPath(file));
     } else if (imagePath != null) {
         // อนุญาตให้เคลียร์ path ได้ด้วยการส่งค่าว่าง
         p.setImagePath(imagePath.isBlank() ? null : imagePath);
     }
     p = productService.save(p);
     return ApiMapper.toDto(p, redeemCodeService.availableStock(p));
 }

 // ---------- แอดมิน: ปิดการขาย /  ----------
 @DeleteMapping("/admin/{id}")
 public void softDelete(@PathVariable Long id){ productService.softDelete(id); }

 /*
//---------- แอดมิน: ปิดการขาย / Toggle(เหมือนข้างบน active) ----------
 @PostMapping("/admin/{id}/toggle")
 public void toggle(@PathVariable Long id){ productService.toggleActive(id); }
*/
 
 // ---------- ช่วยเซฟไฟล์ ----------
 private String storeFileAndGetWebPath(MultipartFile file) throws IOException {
     String ext = Optional.ofNullable(file.getOriginalFilename())
             .filter(f -> f.contains("."))
             .map(f -> f.substring(f.lastIndexOf('.')))
             .orElse("");
     String filename = java.util.UUID.randomUUID() + ext;

     Path base = Paths.get(uploadDir);
     Files.createDirectories(base);
     Path target = base.resolve(filename);
     try (InputStream in = file.getInputStream()) {
         Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
     }
     // สมมุติ map static resource เป็น /uploads/**
     return "/uploads/" + filename;
 }
}
