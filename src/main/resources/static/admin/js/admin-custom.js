/**
 * Sun Booking Tours - Admin Custom JavaScript
 * Author: Sun Booking Tours Team
 * Version: 1.0.0
 */

(function ($) {
  "use strict";

  /**
   * Initialize on document ready
   */
  $(document).ready(function () {
    // Initialize all components
    initTooltips();
    initPopovers();
    initConfirmDialogs();
    initAjaxForms();
    initDataTables();
    handleActiveMenuState();

    // Debug: Check if Bootstrap is loaded
    if (typeof $.fn.dropdown === "undefined") {
      console.error("Bootstrap dropdown plugin is not loaded!");
    } else {
      console.log("Bootstrap dropdown plugin loaded successfully");
    }

    console.log("Admin custom scripts loaded successfully");
  });

  /**
   * Initialize Bootstrap tooltips
   */
  function initTooltips() {
    $('[data-toggle="tooltip"]').tooltip();
  }

  /**
   * Initialize Bootstrap popovers
   */
  function initPopovers() {
    $('[data-toggle="popover"]').popover();
  }

  /**
   * Handle confirmation dialogs for delete/dangerous actions
   */
  function initConfirmDialogs() {
    $(document).on("click", "[data-confirm]", function (e) {
      var message =
        $(this).data("confirm") ||
        "Are you sure you want to perform this action?";
      if (!confirm(message)) {
        e.preventDefault();
        return false;
      }
    });
  }

  /**
   * Handle AJAX form submissions
   */
  function initAjaxForms() {
    $(".ajax-form").on("submit", function (e) {
      e.preventDefault();
      var $form = $(this);
      var url = $form.attr("action");
      var method = $form.attr("method") || "POST";
      var data = $form.serialize();

      $.ajax({
        url: url,
        method: method,
        data: data,
        beforeSend: function () {
          $form.find('button[type="submit"]').prop("disabled", true);
          showLoader($form);
        },
        success: function (response) {
          handleAjaxSuccess($form, response);
        },
        error: function (xhr) {
          handleAjaxError($form, xhr);
        },
        complete: function () {
          $form.find('button[type="submit"]').prop("disabled", false);
          hideLoader($form);
        },
      });
    });
  }

  /**
   * Initialize DataTables if present
   */
  function initDataTables() {
    if ($.fn.DataTable) {
      $(".data-table").DataTable({
        responsive: true,
        autoWidth: false,
        language: {
          search: "_INPUT_",
          searchPlaceholder: "Search records...",
        },
        pageLength: 25,
        lengthMenu: [
          [10, 25, 50, 100, -1],
          [10, 25, 50, 100, "All"],
        ],
      });
    }
  }

  /**
   * Handle active menu state based on current URL
   */
  function handleActiveMenuState() {
    var currentPath = window.location.pathname;
    $(".nav-sidebar .nav-link").each(function () {
      var $link = $(this);
      var href = $link.attr("href");

      if (href && currentPath.startsWith(href) && href !== "#") {
        $link.addClass("active");

        // If it's a submenu item, expand parent
        var $parent = $link.closest(".nav-treeview");
        if ($parent.length) {
          $parent.prev(".nav-link").addClass("active");
          $parent.closest(".nav-item").addClass("menu-open");
          $parent.css("display", "block");
        }
      }
    });
  }

  /**
   * Show loading overlay on element
   */
  function showLoader($element) {
    var $loader = $(
      '<div class="overlay"><i class="fas fa-2x fa-sync-alt fa-spin"></i></div>'
    );
    $element.css("position", "relative").append($loader);
  }

  /**
   * Hide loading overlay
   */
  function hideLoader($element) {
    $element.find(".overlay").remove();
  }

  /**
   * Handle successful AJAX response
   */
  function handleAjaxSuccess($form, response) {
    if (response.message) {
      showNotification("success", response.message);
    }
    if (response.redirect) {
      window.location.href = response.redirect;
    } else if (response.reload) {
      window.location.reload();
    }
  }

  /**
   * Handle AJAX error response
   */
  function handleAjaxError($form, xhr) {
    var message = "An error occurred. Please try again.";
    if (xhr.responseJSON && xhr.responseJSON.message) {
      message = xhr.responseJSON.message;
    } else if (xhr.responseText) {
      try {
        var error = JSON.parse(xhr.responseText);
        message = error.message || message;
      } catch (e) {
        message = xhr.statusText || message;
      }
    }
    showNotification("error", message);
  }

  /**
   * Show toast notification
   */
  function showNotification(type, message) {
    var icon = type === "success" ? "check" : "exclamation-triangle";
    var bgClass = type === "success" ? "bg-success" : "bg-danger";

    if (typeof $.toast === "function") {
      $.toast({
        heading: type === "success" ? "Success" : "Error",
        text: message,
        icon: type,
        position: "top-right",
        showHideTransition: "slide",
        hideAfter: 5000,
      });
    } else {
      // Fallback to alert if toast plugin not available
      alert(message);
    }
  }

  /**
   * Utility: Format number with thousands separator
   */
  window.formatNumber = function (num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

  /**
   * Utility: Format currency
   */
  window.formatCurrency = function (amount, currency = "$") {
    return currency + formatNumber(parseFloat(amount).toFixed(2));
  };

  /**
   * Utility: Truncate text
   */
  window.truncateText = function (text, length = 100) {
    if (text.length <= length) return text;
    return text.substr(0, length) + "...";
  };

  /**
   * Handle dynamic form field additions (for repeater fields)
   */
  $(document).on("click", ".add-field", function (e) {
    e.preventDefault();
    var $template = $($(this).data("template"));
    var $container = $($(this).data("container"));
    var $clone = $template.clone().removeClass("d-none");
    $container.append($clone);
  });

  /**
   * Handle dynamic form field removal
   */
  $(document).on("click", ".remove-field", function (e) {
    e.preventDefault();
    $(this).closest(".form-row, .form-group").remove();
  });

  /**
   * Auto-hide alerts after 5 seconds
   */
  setTimeout(function () {
    $(".alert:not(.alert-permanent)").fadeOut("slow", function () {
      $(this).remove();
    });
  }, 5000);

  /**
   * Handle image preview before upload
   */
  $(document).on("change", 'input[type="file"].image-preview', function (e) {
    var file = e.target.files[0];
    var $preview = $($(this).data("preview"));

    if (file && file.type.match("image.*")) {
      var reader = new FileReader();
      reader.onload = function (e) {
        $preview.attr("src", e.target.result).removeClass("d-none");
      };
      reader.readAsDataURL(file);
    }
  });

  /**
   * Toggle password visibility
   */
  $(document).on("click", ".toggle-password", function () {
    var $input = $($(this).data("target"));
    var $icon = $(this).find("i");

    if ($input.attr("type") === "password") {
      $input.attr("type", "text");
      $icon.removeClass("fa-eye").addClass("fa-eye-slash");
    } else {
      $input.attr("type", "password");
      $icon.removeClass("fa-eye-slash").addClass("fa-eye");
    }
  });

  /**
   * Character counter for textareas
   */
  $("textarea[maxlength]").each(function () {
    var $textarea = $(this);
    var maxLength = $textarea.attr("maxlength");
    var $counter = $(
      '<small class="form-text text-muted char-counter"></small>'
    );
    $textarea.after($counter);

    $textarea
      .on("input", function () {
        var remaining = maxLength - $(this).val().length;
        $counter.text(remaining + " characters remaining");
      })
      .trigger("input");
  });

  /**
   * ========================================
   * BOOKING MANAGEMENT SPECIFIC SCRIPTS
   * ========================================
   */

  /**
   * Initialize Booking Status Update Form
   * Handles dynamic validation for cancel reason field
   */
  function initBookingStatusForm() {
    var $statusSelect = $("#status");
    var $cancelReasonGroup = $("#cancelReasonGroup");
    var $cancelRequired = $("#cancelRequired");
    var $cancelReason = $("#cancelReason");
    var $form = $statusSelect.closest("form");

    if ($statusSelect.length === 0) {
      return; // Not on booking status page
    }

    // Toggle cancel reason field based on status selection
    $statusSelect.on("change", function () {
      var selectedStatus = $(this).val();
      if (selectedStatus === "CANCELLED") {
        $cancelReasonGroup.show();
        $cancelRequired.show();
        $cancelReason.attr("required", true);
      } else {
        $cancelReasonGroup.show();
        $cancelRequired.hide();
        $cancelReason.attr("required", false);
      }
    });

    // Trigger on page load to handle pre-selected value
    $statusSelect.trigger("change");

    // Confirmation before submit
    $form.on("submit", function (e) {
      var status = $statusSelect.val();
      var paymentStatus = $("#paymentStatus").val();

      if (status === "CANCELLED") {
        var cancelReason = $cancelReason.val().trim();
        if (cancelReason === "") {
          e.preventDefault();
          alert(
            "Please enter a cancel reason when changing status to CANCELLED."
          );
          return false;
        }
      }

      return confirm("Are you sure you want to update this booking status?");
    });
  }

  // Initialize booking status form on page load
  initBookingStatusForm();

  /**
   * ========================================
   * DASHBOARD CHARTS INITIALIZATION
   * ========================================
   */

  /**
   * Initialize dashboard charts
   * This function is called from dashboard.html with server-side data
   */
  window.initDashboardCharts = function (monthlyRevenue, revenueBreakdown) {
    if (typeof Chart === "undefined") {
      console.error("Chart.js is not loaded!");
      return;
    }

    // Monthly Revenue Line Chart
    var monthlyLabels = Object.keys(monthlyRevenue);
    var monthlyValues = Object.values(monthlyRevenue);

    var ctxLine = document.getElementById("monthlyRevenueChart");
    if (ctxLine) {
      new Chart(ctxLine.getContext("2d"), {
        type: "line",
        data: {
          labels: monthlyLabels,
          datasets: [
            {
              label: "Revenue ($)",
              data: monthlyValues,
              borderColor: "rgb(60, 141, 188)",
              backgroundColor: "rgba(60, 141, 188, 0.1)",
              tension: 0.4,
              fill: true,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: true,
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: function (value) {
                  return "$" + value.toFixed(2);
                },
              },
            },
          },
        },
      });
    }

    // Revenue Breakdown Pie Chart
    var breakdownLabels = Object.keys(revenueBreakdown);
    var breakdownValues = Object.values(revenueBreakdown);

    var ctxPie = document.getElementById("revenueBreakdownChart");
    if (ctxPie) {
      new Chart(ctxPie.getContext("2d"), {
        type: "pie",
        data: {
          labels: breakdownLabels,
          datasets: [
            {
              data: breakdownValues,
              backgroundColor: [
                "rgb(0, 166, 90)", // PAID - green
                "rgb(243, 156, 18)", // PENDING - yellow
                "rgb(221, 75, 57)", // FAILED - red
                "rgb(153, 153, 153)", // REFUNDED - gray
              ],
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: "bottom",
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  var label = context.label || "";
                  var value = context.parsed || 0;
                  return label + ": $" + value.toFixed(2);
                },
              },
            },
          },
        },
      });
    }

    console.log("Dashboard charts initialized successfully");
  };
})(jQuery);
