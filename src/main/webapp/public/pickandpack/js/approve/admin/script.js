var SERVER_URL = "approve-doc-list";
var __SUB_LINK = null;
var arr_id = {};

var CURRENT_PAGE = 0;
var TOTAL_PAGE = 20;
var PAGE_SIZE = 0;
var PAGI_CURRENT_VALUE = null;

var IS_SHOWALL = false;
var IS_REFRESH = false;
var IS_SEARCH = false;
var CAN_SEARCH = false;

var __allWhCodePage = null;
var __allShelfCodePage = null;

var tmpKeyID = null;
var tmpRefCode = null;
var tmpFromDetail = null;
var data_show = [];

$(function () {
    $(document).ready(function () {

        __SUB_LINK = $("#h_sub_link").val();
        __getConfigsPage();
        objErr = $("#content-error-list").clone();
        objPagination = $("#content-pagination-list").clone();
        $("#txt-search-date-from").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-to").inputmask('dd-mm-yyyy', {'placeholder': 'วว-ดด-ปปปป'});
        $("#txt-search-date-from").datepicker(__getDatePickerOptionx());
        $("#txt-search-date-to").datepicker(__getDatePickerOptionx());

        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());

        setTimeout(function () {
            $("#content-search-box").fadeIn("fast");
            $("#content-table-box").fadeIn("fast");
            $("#content-pagination-box").fadeIn("fast");
            $("#content-loading-box").fadeOut("fast");
            _refreshPAGE();
        }, 4000);
        setTimeout(function () {
          //  _refreshPAGEAuto();
        }, 20000);
    });

    $("#content-search-box").on("keypress", "#txt-search", function (e) {
        if (e.keyCode === 13) {
            IS_SHOWALL = false;
            IS_SEARCH = true;
            CURRENT_PAGE = 0;
            var ref_code = $("#txt-search").val().trim();
            var sendData = {};
            sendData = {
                wh_code: __addCommaData(__allWhCodePage.split(','), 2),
                shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
            };
            var from_date = __formatDate($("#txt-search-date-from").val());
            var to_date = __formatDate($("#txt-search-date-to").val());
            if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
                sendData.from_date = from_date;
                sendData.to_date = to_date;
            }
            if (ref_code !== "") {
                sendData.ref_code = ref_code;
            }
            var doc_type = $("#type-select").val().trim();
            if (doc_type !== "") {
                sendData.doc_type = doc_type;
            }
            _getMainDetail(sendData);
        }
    });

    $("#content-search-box").on("input", "#type-select", function () {
        IS_SHOWALL = false;
        IS_SEARCH = true;
        CURRENT_PAGE = 0;
        var ref_code = $("#txt-search").val().trim();

        var sendData = {};
        sendData = {
            wh_code: __addCommaData(__allWhCodePage.split(','), 2),
            shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
        };
        var from_date = __formatDate($("#txt-search-date-from").val());
        var to_date = __formatDate($("#txt-search-date-to").val());
        if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
            sendData.from_date = from_date;
            sendData.to_date = to_date;
        }
        if (ref_code !== "") {
            sendData.ref_code = ref_code;
        }
        var doc_type = $("#type-select").val().trim();
        if (doc_type !== "") {
            sendData.doc_type = doc_type;
        }
        _getMainDetail(sendData);
    });

    $("#content-search-box").on("click", "#btn-search", function () {
        IS_SHOWALL = false;
        IS_SEARCH = true;
        CURRENT_PAGE = 0;
        var ref_code = $("#txt-search").val().trim();
        var sendData = {};
        sendData = {
            wh_code: __addCommaData(__allWhCodePage.split(','), 2),
            shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
        };
        if (ref_code !== "") {
            sendData.ref_code = ref_code;
        }
        var from_date = __formatDate($("#txt-search-date-from").val());
        var to_date = __formatDate($("#txt-search-date-to").val());
        if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
            sendData.from_date = from_date;
            sendData.to_date = to_date;
        }
        var doc_type = $("#type-select").val().trim();
        if (doc_type !== "") {
            sendData.doc_type = doc_type;
        }
        var doc_type = $("#type-select").val().trim();
        if (doc_type !== "") {
            sendData.doc_type = doc_type;
        }
        _getMainDetail(sendData);
    });

    $("#content-search-box").on("change", "#txt-search-date-from", function () {
        if (CAN_SEARCH) {
            __updateActiveTimes();

            CURRENT_PAGE = 0;
            IS_SHOWALL = false;
            arr_id = {};
            _refreshPAGE();
        }
    });

    $("#content-search-box").on("change", "#txt-search-date-to", function () {
        if (CAN_SEARCH) {
            __updateActiveTimes();

            CURRENT_PAGE = 0;
            IS_SHOWALL = false;
            arr_id = {};
            _refreshPAGE();
        }
    });


    $("#content-pagination-box").on("keydown", "#txt-pagination", function (e) {
        // Allow: backspace, delete, tab, escape, enter and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
                // Allow: Ctrl+A, Command+A
                        (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
                        // Allow: home, end, left, right, down, up
                                (e.keyCode >= 35 && e.keyCode <= 40)) {
                    // let it happen, don't do anything
                    return;
                }
                // Ensure that it is a number and stop the keypress
                if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                    e.preventDefault();
                }
            });

    $("#content-pagination-box").on("keypress", "#txt-pagination", function (e) {
        if (e.keyCode === 13) {
            if ($(this).val() <= PAGE_SIZE && $(this).val() > 0) {
                CURRENT_PAGE = $(this).val();
                _refreshPAGE();
            } else {
                $(this).val(PAGI_CURRENT_VALUE);
            }
            $(this).blur();
        }
    });

    $("#content-pagination-box").on("focusin", "#txt-pagination", function () {
        PAGI_CURRENT_VALUE = $(this).val();
        $(this).val('');
    });

    $("#content-pagination-box").on("focusout", "#txt-pagination", function () {
        $(this).val(PAGI_CURRENT_VALUE);
    });

    $("#content-pagination-box").on("click", ".btn-pagination", function () {
        var page_id = $(this).attr("page-id");
        if (page_id >= 0) {
            CURRENT_PAGE = page_id;
            _refreshPAGE();
        }
    });


    $("#content-table-box").on("change", "#sel-table-rows", function () {
        __updateActiveTimes();
        CURRENT_PAGE = 0;
        TOTAL_PAGE = parseInt($(this).val());
        _refreshPAGE();
    });

    $("#content-table-box").on("click", "#btn-show-all", function () {
        __updateActiveTimes();

        IS_SHOWALL = true;
        CAN_SEARCH = false;
        CURRENT_PAGE = 0;
        $("#txt-search").val('');
        $("#txt-scan-doc").val('');
        $("#txt-search-date-from").datepicker('update', new Date());
        $("#txt-search-date-to").datepicker('update', new Date());
        _refreshPAGE();
    });

    $("#content-table-box").on("click", "#btn-refresh", function () {
        __updateActiveTimes();
        IS_REFRESH = true;
        _refreshPAGE();
    });


    $("#content-table-list").on("click", ".btn-more", function () {
        __updateActiveTimes();
        var key_id = $(this).parents("tr").attr("key_id");

        if ($.isEmptyObject(arr_id[key_id])) {
            arr_id[key_id] = "1";
            _getSubDetail(key_id);
        } else {
            switch (arr_id[key_id]) {
                case "0":
                    arr_id[key_id] = "1";
                    _getSubDetail(key_id);
                    break;
                case "1":
                    arr_id[key_id] = "0";
                    $("#" + key_id).hide('fast');
                    break;
            }
        }
    });

    $("#content-table-list").on("click", ".btn-approve", function () {
        __updateActiveTimes();
        var key_id = $(this).parents("tr").attr("key_id");
        var trans = $(this).parents("tr").attr("trans");
        swal(__getDialogOption('อนุมัติ', "เอกสารเลขที่ " + key_id + " ใช่หรือไม่?")).then(function () {
            _approveDoc(key_id, trans);
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });

    });

    $("#content-table-list").on("click", "#btn-cancel-doc", function () {
        __updateActiveTimes();
        tmpKeyID = $(this).parents("tr").attr("key_id");
        tmpRefCode = $(this).parents("tr").attr("ref_code");
        tmpFromDetail = $(this).parents("tr").attr("from_detail");
        swal(__getDialogOption('ยกเลิกเอกสาร', "เอกสารเลขที่ " + tmpKeyID + " ใช่หรือไม่?")).then(function () {
            _cancelConfirm();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });

    $("#content-table-list").on("click", "#btn-confirm-doc", function () {
        __updateActiveTimes();
        tmpKeyID = $(this).parents("tr").attr("key_id");
        tmpFromDetail = $(this).parents("tr").attr("from_detail");
        swal(__getDialogOption('กลับไปอนุมัติใบจัดอีกครั้ง', "เอกสารเลขที่ " + tmpKeyID + " ใช่หรือไม่?")).then(function () {
            _reConfirm();
        }, function (dissmiss) {
            if (dissmiss === "cancel") {
            }
        });
    });
});

function _refreshPAGEAuto() {
    console.log("Refresh")
    if (__allWhCodePage === null || __allShelfCodePage === null) {
        __getConfigsPage();
    }
    var sendData = {};
    sendData = {

        wh_code: __addCommaData(__allWhCodePage.split(','), 2),
        shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
    };
    var doc_type = $("#type-select").val().trim();

    var doc_no = $("#txt-search").val();
    console.log(sendData)

    if (doc_no !== "") {
        sendData.doc_no = doc_no;
    } else {
        var from_date = __formatDate($("#txt-search-date-from").val());
        var to_date = __formatDate($("#txt-search-date-to").val());
        if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
            sendData.from_date = from_date;
            sendData.to_date = to_date;
        }
        if (doc_type !== "") {
            sendData.doc_type = doc_type;
        }
    }
    console.log(sendData)
    _getMainDetailOnRefresh(sendData);

    CAN_SEARCH = true;

    setTimeout(function () {
        _refreshPAGEAuto();
    }, 30000);
}


function _refreshPAGE() {
    console.log("Refresh")
    if (__allWhCodePage === null || __allShelfCodePage === null) {
        __getConfigsPage();
    }
    var sendData = {};
    sendData = {
        wh_code: __addCommaData(__allWhCodePage.split(','), 2),
        shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
    };
    var doc_type = $("#type-select").val().trim();

    var doc_no = $("#txt-search").val();
    console.log(sendData)

    if (doc_no !== "") {
        sendData.doc_no = doc_no;
    } else {
        var from_date = __formatDate($("#txt-search-date-from").val());
        var to_date = __formatDate($("#txt-search-date-to").val());
        if (from_date !== "NaN-NaN-NaN" && to_date !== "NaN-NaN-NaN") {
            sendData.from_date = from_date;
            sendData.to_date = to_date;
        }
        if (doc_type !== "") {
            sendData.doc_type = doc_type;
        }
    }
    console.log(sendData)
    _getMainDetail(sendData);

    CAN_SEARCH = true;


}

function __updateActiveTimes() {
    var myDate = new Date();
    myDate.setTime(myDate.getTime() + 15 * 60 * 1000);
    document.cookie = "activetime=yes; expires=" + myDate.toUTCString();
}

function __formatDate(date) {
    var d = new Date(date.split("-").reverse().join("-")),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();
    if (month.length < 2) {
        month = '0' + month;
    }
    if (day.length < 2) {
        day = '0' + day;
    }
    return [year, month, day].join('-');
}

function __alertToastMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
    toastr.options = {
        closeButton: false,
        debug: false,
        newestOnTop: true,
        progressBar: false,
        /*
         positionClass
         toast-top-right, toast-top-left, toast-bottom-right,
         toast-bottom-left, toast-top-full-width, toast-bottom-full-width, toast-top-center, toast-bottom-center
         */
        positionClass: "toast-top-right",
        preventDuplicates: false,
        onclick: null,
        showDuration: "300",
        hideDuration: "1000",
        timeOut: "5000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    };
    toastr[strType](strMessage);
}

function __getConfigsPage() {
    var sendData = {};
    $.ajax({
        url: __SUB_LINK + "global-services-1" + "?action_name=get_configs",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                console.log("here")
                __allWhCodePage = [response.arr_wh_code].join(",");
                __allShelfCodePage = [response.arr_shelf_code].join(",");
            } else {
                __showErrorBox(response);
            }
        }
    });
}
function __addCommaData(arr, type) {
    var strResult = "";
    switch (parseInt(type)) {
        case 1:
            $.each(arr, function (key, val) {
                if (key === 0) {
                    strResult += val;
                } else {
                    strResult += "," + val;
                }
            });
            break;
        case 2:
            strResult = "(";
            $.each(arr, function (key, val) {
                if (key === 0) {
                    strResult += "'" + val + "'";
                } else {
                    strResult += ",'" + val + "'";
                }
            });
            strResult += ")";
            break;
    }
    return strResult;
}

function __getDialogOption(strTitle, strMessage) {
    return {
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: strTitle,
        text: strMessage,
        type: 'question',
        showCancelButton: true,
        confirmButtonColor: '#00a65a',
        cancelButtonColor: '#d33',
        confirmButtonText: 'ยืนยัน',
        cancelButtonText: 'ยกเลิก',
        animation: false,
        customClass: 'animated bounceIn'
    };
}

function _getMainDetail(sendData) {
    console.log(sendData)
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {
            $("#content-table-list").html(__loadingText(12));
            $("#content-pagination-box").empty();
        },
        success: function (response) {
            $("#content-table-list").empty();
            if (response.success) {
                console.log(response.data)
                data_show = response.data
                _displayTable(data_show)
                if (response.row_count > 0) {
                    $("#content-pagination-box").append(__createPagination(response.row_count));
                }
            } else {
                $("#content-table-list").html(response.data);
                __showErrorBox(response);
            }
        },
        complete: function () {
            CAN_SEARCH = true;
            if (IS_REFRESH || IS_SEARCH) {
                IS_REFRESH = false;
                IS_SEARCH = false;
                if (!$.isEmptyObject(arr_id)) {
                    _displayDetail(arr_id);
                }
            }
            // $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}


function _getMainDetailOnRefresh(sendData) {
    console.log(sendData)
    var _offset = 0;
    if (CURRENT_PAGE > 0) {
        _offset = (CURRENT_PAGE - 1) * TOTAL_PAGE;
    }
    sendData.offset = _offset;
    sendData.limit = TOTAL_PAGE;

    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_main_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        beforeSend: function () {

           // $("#content-pagination-box").empty();
        },
        success: function (response) {
            // $("#content-table-list").empty();
            if (response.success) {
                console.log(response.data)

                if (data_show.length == 0) {
                    $("#content-table-list").html(__loadingText(12));
                    data_show = response.data
                    _displayTable(data_show)
                } else {

                    response.data.forEach((data) => {
                        var findData = data_show.filter(function (item) {
                            return item.doc_no == data.doc_no;
                        });
                        if (findData.length == 0) {

                            console.log("found ")
                            data_show.push(data)
                            _appenTable(data);
                        }
                    });
                    data_show.forEach((data, index) => {
                        var findData = response.data.filter(function (item) {
                            return item.doc_no == data.doc_no;
                        });
                        if (findData.length == 0) {
                            console.log("remove ")
                            data_show.splice(index, 1)
                            $('.' + data.doc_no).remove();
                        }
                    });

                }
                //$("#content-table-list").html(response.data);

                /*if (__checkPermPrint()) {
                 $("#content-table-list").find(".is_print").removeAttr("disabled");
                 }*/
                if (response.row_count > 0) {
                    $("#content-pagination-box").html(__createPagination(response.row_count));
                }
            } else {
                $("#content-table-list").html(response.data);
                __showErrorBox(response);
            }
        },
        complete: function () {
            CAN_SEARCH = true;
            if (IS_REFRESH || IS_SEARCH) {
                IS_REFRESH = false;
                IS_SEARCH = false;
                if (!$.isEmptyObject(arr_id)) {
                    _displayDetail(arr_id);
                }
            }
            // $("html, body").animate({scrollTop: 0}, "fast");
        }
    });
}


function _appenTable(data) {
    var __rsHTML = "";
    var _docType = "";
    if (data.trans_flag == "44") {
        _docType = "ขายสินค้า";
    } else if (data.trans_flag == "36") {
        _docType = "ใบสั่งขาย";
    } else if (data.trans_flag == "34") {
        _docType = "สั่งซื้อ/สั่งจอง";
    } else if (data.trans_flag == "30") {
        _docType = "เสนอราคา";
    }


    __rsHTML += "<tr class='" + data.doc_no + "' key_id='" + data.doc_no + "' trans='" + data.trans_flag + "'>";
    __rsHTML += "<td><h5>" + _docType + "</h5></td>";
    __rsHTML += "<td><h5>" + data.doc_date + "</h5></td>";
    __rsHTML += "<td><h5>" + data.doc_no + "</h5></td>";
    __rsHTML += "<td><h5>" + data.branch_code + " ~ " + data.branch_name + "</h5></td>";
    __rsHTML += "<td><h5>" + data.cust_code + " ~ " + data.cust_name + "</h5></td>";
    __rsHTML += "<td><h5>" + data.sale_type + "</h5></td>";
    __rsHTML += "<td><h5>" + data.send_type + "</h5></td>";
    __rsHTML += "<td><button type='button' class='btn btn-info btn-flat btn-more'>รายละเอียด</button></td>";
    __rsHTML += "<td><button type='button' class='btn btn-success btn-flat btn-approve'>อนุมัติ</button></td>";
    __rsHTML += "</tr>";

    __rsHTML += "<tr id='" + data.doc_no + "' class='" + data.doc_no + "' style='display: none;'></tr>";

    $("#content-table-list").prepend(__rsHTML);
}

function _displayTable(data) {
    var __rsHTML = "";

    for (var i = 0; i < data.length; i++) {

        var _docType = "";
        if (data[i].trans_flag == "44") {
            _docType = "ขายสินค้า";
        } else if (data[i].trans_flag == "36") {
            _docType = "ใบสั่งขาย";
        } else if (data[i].trans_flag == "34") {
            _docType = "สั่งซื้อ/สั่งจอง";
        } else if (data[i].trans_flag == "30") {
            _docType = "เสนอราคา";
        }


        __rsHTML += "<tr class='" + data[i].doc_no + "' key_id='" + data[i].doc_no + "' trans='" + data[i].trans_flag + "'>";
        __rsHTML += "<td><h5>" + _docType + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].doc_date + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].doc_no + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].branch_code + " ~ " + data[i].branch_name + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].cust_code + " ~ " + data[i].cust_name + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].sale_type + "</h5></td>";
        __rsHTML += "<td><h5>" + data[i].send_type + "</h5></td>";
        __rsHTML += "<td><button type='button' class='btn btn-info btn-flat btn-more'>รายละเอียด</button></td>";
        __rsHTML += "<td><button type='button' class='btn btn-success btn-flat btn-approve'>อนุมัติ</button></td>";
        __rsHTML += "</tr>";

        __rsHTML += "<tr id='" + data[i].doc_no + "' class='" + data[i].doc_no + "' style='display: none;'></tr>";
    }


    if (__rsHTML == "") {
        __rsHTML = "<tr><td colspan='14'><h5>ไม่พบข้อมูล</h5></td></tr>";
    }

    $("#content-table-list").html(__rsHTML);
}



function __createPagination(total_records) {
    var objPagiList = objPagination.clone();
    $("#content-pagination").empty();
    var page = parseInt(CURRENT_PAGE) === 0 ? 1 : CURRENT_PAGE;
    var total_pages = total_records > 0 ? parseInt(total_records / TOTAL_PAGE) : 0;
    if (parseInt(total_pages) === 0) {
        total_pages = 1;
    } else {
        var total_pages2 = total_records > 0 ? parseFloat(total_records / TOTAL_PAGE) : 0.00;
        total_pages2 = total_pages2.toFixed(2); // ทำให้เป็นทศนิยม 2 ตำแหน่ง
        var split_total_page2 = total_pages2.toString().split('.');
        if (split_total_page2[1] > 0) {
            total_pages += 1;
        }
    }

    PAGE_SIZE = total_pages;
    // CREATE HTML
    if (parseInt(page) === 1) {
        objPagiList.find("#btn-pagi-first").attr('page-number', -1).addClass("disabled");
        objPagiList.find("#btn-pagi-previous").attr("page-number", -1).addClass("disabled");
    } else {
        objPagiList.find("#btn-pagi-first").attr('page-id', 1).addClass("btn-pagination");
        objPagiList.find("#btn-pagi-previous").attr("page-id", parseInt(page) - 1).addClass("btn-pagination");
    }
    if (parseInt(page) === parseInt(total_pages)) {
        objPagiList.find("#btn-pagi-next").attr("page-id", -1).addClass("btn-pagination disabled");
        objPagiList.find("#btn-pagi-last").attr('page-id', -1).addClass("btn-pagination disabled");
    } else {
        objPagiList.find("#btn-pagi-next").attr("page-id", parseInt(page) + 1).addClass("btn-pagination");
        objPagiList.find("#btn-pagi-last").attr('page-id', total_pages).addClass("btn-pagination");
    }

    objPagiList.find("#txt-pagination").val("หน้า " + page + " ถึง " + total_pages);
    // return HTML
    return objPagiList;
}


function __loadingText(width) {
    return "<tr><td colspan='" + width + "'><p class='text-center'><span class='fa fa-spinner fa-spin fa-2x fa-fw'></span> กำลังโหลดข้อมูล...</p></td></tr>";
}
function __showErrorBox(response) {
    __alertDialogMessage("การดึงข้อมูลไม่สำเร็จ", "error");
    var tmpErr = objErr.clone();
    tmpErr.find("#txt-err-title").text(response.err_title);
    tmpErr.find("#txt-err-msg").text(response.err_msg);
    $("#content-error-box").empty().append(tmpErr).show('fast');
}

function _approveDoc(KEY_ID, trans) {

    var sendData = {
        doc_no: KEY_ID,
        trans_flag: trans,
        wh_code: __addCommaData(__allWhCodePage.split(','), 2),
        shelf_code: __addCommaData(__allShelfCodePage.split(','), 2)
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=approve_doc_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("อนุมัติเอกสารเรียบร้อยแล้ว", "success");
                _refreshPAGE();
                //$("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
            } else {
                //$("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
                __showErrorBox(response);
            }
        }
    });
}

function _getSubDetail(KEY_ID) {
    var sendData = {
        doc_no: KEY_ID,
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=get_sub_detail",
        type: "GET",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                $("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
            } else {
                $("#content-table-list").find("#" + KEY_ID).html(response.data).show('fast');
                __showErrorBox(response);
            }
        }
    });
}

function _displayDetail(key_id) {
    $.each(key_id, function (key, obj) {
        if (obj === "1") {
            _getSubDetail(key);
        }
    });
}

function _cancelConfirm() {
    var sendData = {};
    if (tmpFromDetail === "pp_trans") {
        sendData.doc_no = tmpKeyID;
    } else {
        sendData.ref_code = tmpRefCode;
    }
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=cancel_confirm_document",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ยกเลิกเรียบร้อย", "success");
                _refreshPAGE();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function _reConfirm() {
    var sendData = {
        doc_no: tmpKeyID
    };
    $.ajax({
        url: __SUB_LINK + SERVER_URL + "?action_name=re_confirm_document",
        type: "POST",
        data: {data: JSON.stringify(sendData)},
        success: function (response) {
            if (response.success) {
                __alertToastMessage("ทำรายการเรียบร้อย", "success");
                _refreshPAGE();
            } else {
                __showErrorBox(response);
            }
        }
    });
}

function __getDatePickerOptionx() {
    return {
        autoclose: true,
        todayHighlight: true,
        language: 'th',
        format: "dd-mm-yyyy"
    };
}


function __alertDialogMessage(strMessage, strType) { // type => 'success', 'info', 'warning', 'error'
    swal({
        allowOutsideClick: false,
        allowEscapeKey: false,
        title: 'ข้อความระบบ',
        type: strType,
        text: strMessage,
        confirmButtonText: 'ตกลง',
        animation: false,
        customClass: 'animated bounceIn'
    });
}