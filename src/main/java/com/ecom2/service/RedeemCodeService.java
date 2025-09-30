package com.ecom2.service;

import com.ecom2.entity.Product;
import com.ecom2.entity.RedeemCode;
import com.ecom2.repo.RedeemCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedeemCodeService {

    private final RedeemCodeRepository repo;

    public RedeemCodeService(RedeemCodeRepository repo) {
        this.repo = repo;
    }

    /** สต็อกจากจำนวนโค้ด AVAILABLE */
    @Transactional(readOnly = true)
    public long availableStock(Product product) {
        return repo.countByProductAndStatus(product, RedeemCode.Status.AVAILABLE);
    }

    /** สำหรับหน้าแอดมิน: ดึงโค้ดทั้งหมดของสินค้านั้น (จะมีทุกสถานะ) */
    @Transactional(readOnly = true)
    public List<RedeemCode> listAllCodes(Product product) {
        return repo.findByProductOrderByIdAsc(product);
    }

    /** นำเข้าโค้ดครั้งละหลายรายการ */
    @Transactional
    public int importCodes(Product product, List<String> codes) {
        if (codes == null || codes.isEmpty()) return 0;
        List<RedeemCode> toSave = new ArrayList<>(codes.size());
        for (String raw : codes) {
            String code = raw == null ? null : raw.trim();
            if (code == null || code.isEmpty()) continue;
            RedeemCode rc = new RedeemCode();
            rc.setProduct(product);
            rc.setCode(code);
            rc.setStatus(RedeemCode.Status.AVAILABLE);
            toSave.add(rc);
        }
        repo.saveAll(toSave);
        return toSave.size();
    }

    /** เลือกโค้ดแบบล็อกแถว กันชนกันขณะ checkout */
    @Transactional
    public List<RedeemCode> allocateCodes(Product product, int qty) {
        if (qty <= 0) return List.of();
        var page = org.springframework.data.domain.PageRequest.of(0, qty);
        List<RedeemCode> picked = repo.pickAvailableForUpdate(product.getId(), page);
        if (picked.size() < qty) throw new IllegalStateException("Not enough codes for " + product.getName());
        return picked;
    }

    /** mark เป็น ASSIGNED + set เวลา และผูกกับ orderItem ถูกทำใน service เรียกใช้แล้ว */
    @Transactional
    public void markAssigned(List<RedeemCode> codes) {
        LocalDateTime now = LocalDateTime.now();
        for (RedeemCode rc : codes) {
            rc.setStatus(RedeemCode.Status.ASSIGNED);
            rc.setAssignedAt(now);
        }
        repo.saveAll(codes);
    }
}


