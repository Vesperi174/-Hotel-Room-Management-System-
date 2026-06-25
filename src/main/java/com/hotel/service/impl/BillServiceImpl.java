package com.hotel.service.impl;

import com.hotel.common.exception.BusinessException;
import com.hotel.dao.BillDao;
import com.hotel.model.entity.Bill;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.service.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private static final Logger log = LoggerFactory.getLogger(BillServiceImpl.class);

    private final BillDao billDao;

    public BillServiceImpl(BillDao billDao) {
        this.billDao = billDao;
    }

    @Override
    public Bill findById(Integer billId) {
        log.debug("查询账单: billId={}", billId);
        Bill bill = billDao.findById(billId);
        if (bill == null) {
            throw new BusinessException("BIL_001", "账单不存在: billId=" + billId);
        }
        return bill;
    }

    @Override
    public Bill findByCheckinId(Integer checkinId) {
        log.debug("按入住记录查询账单: checkinId={}", checkinId);
        return billDao.findByCheckinId(checkinId);
    }

    @Override
    public List<BillDetailVO> findAllBillDetail() {
        log.debug("查询所有账单详情");
        return billDao.findAllBillDetail();
    }
}