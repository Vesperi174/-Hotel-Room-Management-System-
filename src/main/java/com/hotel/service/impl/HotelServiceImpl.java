package com.hotel.service.impl;

import com.hotel.common.enums.BookingStatus;
import com.hotel.common.enums.CheckinStatus;
import com.hotel.common.enums.RoomStatus;
import com.hotel.common.exception.BusinessException;
import com.hotel.dao.BillDao;
import com.hotel.dao.BookingDao;
import com.hotel.dao.CheckinDao;
import com.hotel.dao.ConsumptionDao;
import com.hotel.dao.CustomerDao;
import com.hotel.dao.RoomDao;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.dto.CheckinRequest;
import com.hotel.model.dto.CheckoutRequest;
import com.hotel.model.entity.Bill;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Checkin;
import com.hotel.model.entity.Consumption;
import com.hotel.model.entity.Customer;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.model.vo.BillDetailVO;
import com.hotel.model.vo.CheckinDetailVO;
import com.hotel.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);

    private final BookingDao bookingDao;
    private final CheckinDao checkinDao;
    private final RoomDao roomDao;
    private final RoomTypeDao roomTypeDao;
    private final CustomerDao customerDao;
    private final ConsumptionDao consumptionDao;
    private final BillDao billDao;

    public HotelServiceImpl(BookingDao bookingDao, CheckinDao checkinDao,
                            RoomDao roomDao, RoomTypeDao roomTypeDao,
                            CustomerDao customerDao, ConsumptionDao consumptionDao,
                            BillDao billDao) {
        this.bookingDao = bookingDao;
        this.checkinDao = checkinDao;
        this.roomDao = roomDao;
        this.roomTypeDao = roomTypeDao;
        this.customerDao = customerDao;
        this.consumptionDao = consumptionDao;
        this.billDao = billDao;
    }

    @Override
    @Transactional
    public Booking booking(Booking booking) {
        validateBooking(booking);
        Customer customer = customerDao.findById(booking.getCustomerId());
        if (customer == null) {
            throw new BusinessException("BKG_001", "客户不存在");
        }
        Room room = roomDao.findById(booking.getRoomId());
        if (room == null) {
            throw new BusinessException("BKG_002", "客房不存在");
        }
        if (!RoomStatus.AVAILABLE.getDescription().equals(room.getRoomStatus())) {
            throw new BusinessException("BKG_003", "客房当前不可预订，状态: " + room.getRoomStatus());
        }

        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.BOOKED.getDescription());
        bookingDao.insert(booking);

        roomDao.updateStatus(booking.getRoomId(), RoomStatus.BOOKED.getDescription());
        log.info("预订成功: bookingId={}, roomNumber={}, customer={}",
                booking.getBookingId(), room.getRoomNumber(), customer.getCustomerName());
        return booking;
    }

    @Override
    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingDao.findById(bookingId);
        if (booking == null) {
            throw new BusinessException("BKG_004", "预订记录不存在");
        }
        if (!BookingStatus.BOOKED.getDescription().equals(booking.getBookingStatus())) {
            throw new BusinessException("BKG_005", "只能取消已预订状态的预订");
        }

        bookingDao.updateStatus(bookingId, BookingStatus.CANCELLED.getDescription());
        roomDao.updateStatus(booking.getRoomId(), RoomStatus.AVAILABLE.getDescription());
        log.info("取消预订: bookingId={}", bookingId);
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingDao.findAll();
    }

    @Override
    @Transactional
    public Checkin checkin(CheckinRequest request) {
        if (request.getCustomerId() == null) {
            throw new BusinessException("CHK_001", "客户ID不能为空");
        }
        if (request.getRoomId() == null) {
            throw new BusinessException("CHK_002", "客房ID不能为空");
        }
        if (request.getUserId() == null) {
            throw new BusinessException("CHK_003", "操作员ID不能为空");
        }

        Room room = roomDao.findById(request.getRoomId());
        if (room == null) {
            throw new BusinessException("CHK_004", "客房不存在");
        }
        if (!RoomStatus.canCheckin(room.getRoomStatus())) {
            throw new BusinessException("CHK_005", "客房当前状态不可入住: " + room.getRoomStatus());
        }

        Checkin checkin = new Checkin();
        checkin.setBookingId(request.getBookingId());
        checkin.setCustomerId(request.getCustomerId());
        checkin.setRoomId(request.getRoomId());
        checkin.setCheckinTime(LocalDateTime.now());
        checkin.setDeposit(request.getDeposit() != null ? request.getDeposit() : BigDecimal.ZERO);
        checkin.setStatus(CheckinStatus.CHECKED_IN.getDescription());
        checkin.setUserId(request.getUserId());
        checkinDao.insert(checkin);

        if (request.getBookingId() != null) {
            bookingDao.updateStatus(request.getBookingId(), BookingStatus.CHECKED_IN.getDescription());
        }
        roomDao.updateStatus(request.getRoomId(), RoomStatus.OCCUPIED.getDescription());
        log.info("入住成功: checkinId={}, roomNumber={}, customerId={}",
                checkin.getCheckinId(), room.getRoomNumber(), request.getCustomerId());
        return checkin;
    }

    @Override
    @Transactional
    public BillDetailVO checkout(CheckoutRequest request) {
        if (request.getCheckinId() == null) {
            throw new BusinessException("CHK_006", "入住记录ID不能为空");
        }
        if (request.getPayMethod() == null || request.getPayMethod().isBlank()) {
            throw new BusinessException("CHK_007", "支付方式不能为空");
        }

        Checkin checkin = checkinDao.findById(request.getCheckinId());
        if (checkin == null) {
            throw new BusinessException("CHK_008", "入住记录不存在");
        }
        if (!CheckinStatus.CHECKED_IN.getDescription().equals(checkin.getStatus())) {
            throw new BusinessException("CHK_009", "该入住记录已退房");
        }

        Room room = roomDao.findById(checkin.getRoomId());
        RoomType roomType = roomTypeDao.findById(room.getTypeId());
        BigDecimal basePrice = roomType.getBasePrice();

        long nights = ChronoUnit.DAYS.between(
                checkin.getCheckinTime().toLocalDate(),
                LocalDateTime.now().toLocalDate());
        nights = Math.max(nights, 1);

        BigDecimal roomCharge = basePrice.multiply(BigDecimal.valueOf(nights));

        List<Consumption> consumptions = consumptionDao.findByCheckinId(checkin.getCheckinId());
        BigDecimal extraCharge = BigDecimal.ZERO;
        for (Consumption c : consumptions) {
            extraCharge = extraCharge.add(c.getItemPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
        }

        BigDecimal totalAmount = roomCharge.add(extraCharge);
        BigDecimal deposit = checkin.getDeposit() != null ? checkin.getDeposit() : BigDecimal.ZERO;
        BigDecimal refund = deposit.subtract(totalAmount);

        Bill bill = new Bill();
        bill.setCheckinId(checkin.getCheckinId());
        bill.setRoomCharge(roomCharge);
        bill.setExtraCharge(extraCharge);
        bill.setDepositPaid(deposit);
        bill.setTotalAmount(totalAmount);
        bill.setRefund(refund.compareTo(BigDecimal.ZERO) > 0 ? refund : BigDecimal.ZERO);
        bill.setPayMethod(request.getPayMethod());
        bill.setBillTime(LocalDateTime.now());
        bill.setUserId(request.getUserId() != null ? request.getUserId() : checkin.getUserId());
        billDao.insert(bill);

        LocalDateTime now = LocalDateTime.now();
        checkinDao.updateStatus(checkin.getCheckinId(), CheckinStatus.CHECKED_OUT.getDescription(), totalAmount);
        roomDao.updateStatus(checkin.getRoomId(), RoomStatus.CLEANING.getDescription());

        log.info("退房成功: checkinId={}, roomCharge={}, extraCharge={}, total={}, refund={}",
                checkin.getCheckinId(), roomCharge, extraCharge, totalAmount, refund);

        BillDetailVO vo = new BillDetailVO();
        vo.setBillId(bill.getBillId());
        vo.setCheckinId(checkin.getCheckinId());
        vo.setRoomCharge(roomCharge);
        vo.setExtraCharge(extraCharge);
        vo.setDepositPaid(deposit);
        vo.setTotalAmount(totalAmount);
        vo.setRefund(refund.compareTo(BigDecimal.ZERO) > 0 ? refund : BigDecimal.ZERO);
        vo.setPayMethod(request.getPayMethod());
        vo.setBillTime(now);
        vo.setCheckinTime(checkin.getCheckinTime());
        vo.setCheckoutTime(now);
        return vo;
    }

    @Override
    public List<CheckinDetailVO> findAllCheckinDetail() {
        log.debug("查询所有入住详情");
        return checkinDao.findAllCheckinDetail();
    }

    @Override
    public List<CheckinDetailVO> findCheckinByStatus(String status) {
        log.debug("按状态查询入住记录: status={}", status);
        return checkinDao.findAllCheckinDetail();
    }

    @Override
    public List<Consumption> findConsumptionsByCheckinId(Integer checkinId) {
        log.debug("查询消费记录: checkinId={}", checkinId);
        return consumptionDao.findByCheckinId(checkinId);
    }

    @Override
    public void addConsumption(Consumption consumption) {
        if (consumption.getCheckinId() == null) {
            throw new BusinessException("CONS_001", "入住ID不能为空");
        }
        if (consumption.getItemName() == null || consumption.getItemName().isBlank()) {
            throw new BusinessException("CONS_002", "消费项目名称不能为空");
        }
        if (consumption.getItemPrice() == null || consumption.getItemPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("CONS_003", "价格必须大于0");
        }
        if (consumption.getQuantity() == null || consumption.getQuantity() <= 0) {
            consumption.setQuantity(1);
        }
        log.info("新增消费记录: checkinId={}, item={}, price={}, qty={}",
                consumption.getCheckinId(), consumption.getItemName(),
                consumption.getItemPrice(), consumption.getQuantity());
        consumptionDao.insert(consumption);
    }

    @Override
    public void deleteConsumption(Integer consId) {
        log.info("删除消费记录: consId={}", consId);
        consumptionDao.delete(consId);
    }

    private void validateBooking(Booking booking) {
        if (booking.getCustomerId() == null) {
            throw new BusinessException("BKG_006", "客户ID不能为空");
        }
        if (booking.getRoomId() == null) {
            throw new BusinessException("BKG_007", "客房ID不能为空");
        }
        if (booking.getExpectedArrival() == null) {
            throw new BusinessException("BKG_008", "预计到达日期不能为空");
        }
        if (booking.getExpectedLeave() == null) {
            throw new BusinessException("BKG_009", "预计离开日期不能为空");
        }
        if (booking.getExpectedLeave().isBefore(booking.getExpectedArrival())) {
            throw new BusinessException("BKG_010", "预计离开日期不能早于到达日期");
        }
    }
}