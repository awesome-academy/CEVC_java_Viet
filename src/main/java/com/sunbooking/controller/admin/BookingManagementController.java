package com.sunbooking.controller.admin;

import static com.sunbooking.constant.ViewConstants.ADMIN_BOOKINGS_DETAIL;
import static com.sunbooking.constant.ViewConstants.ADMIN_BOOKINGS_LIST;
import static com.sunbooking.constant.ViewConstants.ADMIN_BOOKINGS_UPDATE_STATUS;

import java.time.LocalDate;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunbooking.dto.admin.booking.BookingDTO;
import com.sunbooking.dto.admin.booking.BookingDetailDTO;
import com.sunbooking.dto.admin.booking.BookingListDTO;
import com.sunbooking.dto.admin.booking.BookingSearchCriteria;
import com.sunbooking.dto.admin.booking.BookingStatusForm;
import com.sunbooking.entity.BookingStatus;
import com.sunbooking.entity.PaymentStatus;
import com.sunbooking.service.admin.BookingManagementService;

/**
 * Controller for managing bookings in admin panel.
 * Handles listing, viewing, and updating booking status and payment status.
 */
@Controller
@RequestMapping("/admin/bookings")
public class BookingManagementController {

    private static final Logger logger = LoggerFactory.getLogger(BookingManagementController.class);

    @Autowired
    private BookingManagementService bookingManagementService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Display list of all bookings with search and filter options.
     */
    @GetMapping
    public String listBookings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            Model model) {

        logger.debug("Listing bookings - keyword: {}, status: {}, paymentStatus: {}, page: {}",
                keyword, status, paymentStatus, page);

        BookingSearchCriteria criteria = BookingSearchCriteria.builder()
                .keyword(keyword)
                .status(status)
                .paymentStatus(paymentStatus)
                .fromDate(fromDate)
                .toDate(toDate)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<BookingListDTO> bookings = bookingManagementService.getAllBookings(criteria);

        model.addAttribute("bookings", bookings.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookings.getTotalPages());
        model.addAttribute("totalItems", bookings.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("ASC") ? "DESC" : "ASC");
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("bookingStatuses", BookingStatus.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());

        return ADMIN_BOOKINGS_LIST;
    }

    /**
     * Display booking details.
     */
    @GetMapping("/{id}")
    public String viewBooking(@PathVariable Long id, Model model) {
        logger.debug("Viewing booking detail for id: {}", id);

        BookingDetailDTO booking = bookingManagementService.getBookingDetailById(id);

        model.addAttribute("booking", booking);

        return ADMIN_BOOKINGS_DETAIL;
    }

    /**
     * Show form for updating booking status.
     */
    @GetMapping("/{id}/update-status")
    public String showUpdateStatusForm(@PathVariable Long id, Model model) {
        logger.debug("Showing update status form for booking id: {}", id);

        BookingDTO booking = bookingManagementService.getBookingById(id);
        BookingStatusForm form = BookingStatusForm.fromDTO(booking);

        model.addAttribute("booking", booking);
        model.addAttribute("statusForm", form);
        model.addAttribute("bookingStatuses", BookingStatus.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());

        return ADMIN_BOOKINGS_UPDATE_STATUS;
    }

    /**
     * Handle booking status update.
     */
    @PostMapping("/{id}/update-status")
    public String updateBookingStatus(
            @PathVariable Long id,
            @Valid @ModelAttribute("statusForm") BookingStatusForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.debug("Updating booking status for id: {}", id);

        if (result.hasErrors()) {
            logger.warn("Validation errors in booking status form: {}", result.getAllErrors());
            BookingDTO booking = bookingManagementService.getBookingById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("bookingStatuses", BookingStatus.values());
            model.addAttribute("paymentStatuses", PaymentStatus.values());
            return ADMIN_BOOKINGS_UPDATE_STATUS;
        }

        bookingManagementService.updateBookingStatus(id, form);
        addSuccessMessage(redirectAttributes, "success.booking.updated");

        return "redirect:/admin/bookings/" + id;
    }

    /**
     * Utility method to add internationalized success messages.
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String messageKey) {
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
    }
}
