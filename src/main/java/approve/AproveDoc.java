/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package approve;

import cancel.*;
import java.awt.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import utils.ResponseUtil;
import utils._global;
import utils._routine;

/**
 *
 * @author BeamMary
 */
@WebServlet(name = "approve-doc-list", urlPatterns = {"/approve-doc-list"})
public class AproveDoc extends HttpServlet {

    private final _routine __routine = new _routine();
    private String __strDatabaseName;
    private String __strProviderCode;
    private String __strUserCode;
    private String __strSystemID;
    private String __strSessionID;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject objResult = new JSONObject("{'success': false}");
        HttpSession __session = request.getSession();

        if (__session.getAttribute("user_code") == null && __session.getAttribute("user_code").toString().trim().isEmpty()) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", "กรุณาทำการเข้าสู่ระบบ");
            response.getWriter().print(objResult);
            return;
        }

        this.__strDatabaseName = __session.getAttribute("database_name").toString();
        this.__strProviderCode = __session.getAttribute("provider_code").toString();
        this.__strUserCode = __session.getAttribute("user_code").toString();
        this.__strSystemID = __session.getAttribute("system_id").toString();
        this.__strSessionID = __session.getAttribute("session_id").toString();

        String __strActionName = "";
        if (request.getParameter("action_name") != null && !request.getParameter("action_name").isEmpty()) {
            __strActionName = request.getParameter("action_name");
        }
        Connection __conn = null;
        try {
            __conn = __routine._connect(__strDatabaseName, _global.FILE_CONFIG(__strProviderCode));
            switch (__strActionName) {
                case "get_main_detail":
                    System.out.println("heereer");
                    objResult = _getMainDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "get_sub_detail":
                    objResult = _getSubDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "approve_doc_detail":
                    objResult = _approveDocDetail(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
                case "re_confirm_document":
                    objResult = _reConfrim(__conn, ResponseUtil.str2Json(request.getParameter("data")));
                    break;
            }
        } catch (SQLException | JSONException ex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage());
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            objResult.put("err_title", "ข้อผิดพลาดทางระบบ");
            objResult.put("err_msg", "Exception Message: " + ex.getClass().getCanonicalName() + " :: " + ex.getMessage());
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (__conn != null) {
                    __conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        response.getWriter().print(objResult);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public String _readXmlFile(String xmlName) {
        String __readLine = "";
        try {
            // Reader __input = new InputStreamReader(new FileInputStream(xmlName));
            //     BufferedReader __in = new BufferedReader(__input);
            String __tempDir = System.getProperty("java.io.tmpdir");
            BufferedReader __in = new BufferedReader(new InputStreamReader(new FileInputStream(__tempDir + "/" + xmlName), "UTF8"));
            char[] __cBuf = new char[65536];
            StringBuilder __stringBuf = new StringBuilder();
            int __readThisTime = 0;
            while (__readThisTime != -1) {
                try {
                    __readThisTime = __in.read(__cBuf, 0, 65536);
                    __stringBuf.append(__cBuf, 0, __readThisTime);
                } catch (Exception __ex) {
                }
            } // end while
            __readLine = __stringBuf.toString();
            __in.close();
        } catch (Exception __ex) {
            System.out.println("_readXmlFile:" + __ex.getMessage());
            __readLine = __ex.getMessage();
        }
        return __readLine;
    }

    public String _xmlGetNodeValue(Element firstElement, String tagName) {
        try {
            NodeList __firstNameList = firstElement.getElementsByTagName(tagName);
            if (__firstNameList.getLength() > 0) {
                Element __firstNameElement = (Element) __firstNameList.item(0);
                NodeList __textFNList = __firstNameElement.getChildNodes();
                if (__textFNList.getLength() > 0) {
                    Node __getData = __textFNList.item(0);
                    return __getData.getNodeValue().trim();
                }
            }
        } catch (Exception __ex) {
            System.out.println("_xmlGetNodeValue:" + __ex.getMessage());
        }
        return "";
    }

    private JSONObject _getMainDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        Integer __strOffset = !param.isNull("offset") && param.getInt("offset") > 0 ? param.getInt("offset") : 0;
        Integer __strLimit = !param.isNull("limit") && param.getInt("limit") > 0 ? param.getInt("limit") : 0;

        String __strQueryExtends = "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        String __refCode = !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND (doc_no like '%" + param.getString("ref_code") + "%') " : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

        __strQueryExtends += !param.isNull("doc_type") && !param.getString("doc_type").trim().isEmpty() ? " AND (trans_flag ='" + param.getString("doc_type") + "') " : "";
        // __strQueryExtends += !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";

        if (!param.isNull("from_date") && !param.getString("from_date").trim().isEmpty() && !param.isNull("to_date") && !param.getString("to_date").trim().isEmpty()) {
            String __strTmpFromDate = param.getString("from_date");
            String __strTmpToDate = param.getString("to_date");
            if (__strTmpFromDate.equals(__strTmpToDate)) {
                __strQueryExtends += " AND (doc_date = '" + __strTmpFromDate + "') ";
            } else {
                __strQueryExtends += " AND (doc_date BETWEEN '" + __strTmpFromDate + "' AND '" + __strTmpToDate + "') ";
            }
        }

        JSONArray __jsonArr = new JSONArray();
        String __xReloadFile = _readXmlFile("pickandpackconf.xml");
        JSONArray transapprove = new JSONArray(__xReloadFile);
        String _transCondition = " ";
        if (transapprove.length() > 0) {
            _transCondition = " and (";
            for (int i = 0; i < transapprove.length(); i++) {
                JSONObject objJSDataItem = transapprove.getJSONObject(i);
                if (i != 0) {
                    _transCondition += " or (";
                } else {
                    _transCondition += " (";
                }

                _transCondition += " trans_flag = '" + objJSDataItem.getString("trans_flag") + "' ";
                if (!objJSDataItem.getString("send_type").equals("all")) {
                    _transCondition += " and send_type = '" + objJSDataItem.getString("send_type") + "'";
                }
                if (!objJSDataItem.getString("inquiry_type").equals("all")) {
                    _transCondition += " and inquiry_type = '" + objJSDataItem.getString("inquiry_type") + "'";
                }
                if (!objJSDataItem.getString("is_pos").equals("all")) {
                    _transCondition += " and is_pos = '" + objJSDataItem.getString("is_pos") + "'";
                }
                if (!objJSDataItem.getString("doc_success").equals("all")) {
                    _transCondition += " and doc_success = '" + objJSDataItem.getString("doc_success") + "'";
                }
                if (!objJSDataItem.getString("used_status").equals("all")) {
                    _transCondition += " and used_status = '" + objJSDataItem.getString("used_status") + "'";
                }

                _transCondition += ")";
            }
            _transCondition += ")";
        }
        System.out.println("_transCondition " + _transCondition);
        __strQueryExtends = __strQueryExtends.equals("") ? "  " : "  " + __strQueryExtends;
        String __strQUERYDate = "SELECT begin_date FROM tms_config_date WHERE date_flag=2";
        String __strStartDate = "";
        PreparedStatement __stmtGetStartDate;
        __stmtGetStartDate = conn.prepareStatement(__strQUERYDate);
        ResultSet __rsDataStartDate;
        __rsDataStartDate = __stmtGetStartDate.executeQuery();

        while (__rsDataStartDate.next()) {
            __strStartDate = __rsDataStartDate.getString("begin_date");
        }
        __stmtGetStartDate.close();
        __rsDataStartDate.close();
        /* String __strQUERY = "/*[PickAndPack]AUTO APPROVE 1\n"
                + "SELECT is_hold,\n"
                + "       trans_flag,\n"
                + "       doc_date,\n"
                + "       doc_no,\n"
                + "       send_type,"
                + "       sale_type,\n"
                + "       cust_code,\n"
                + "       branch_code,"
                + "       branch_name,"
                + "       cust_name "
                + " FROM\n"
                + "  (SELECT ic_trans.is_hold,\n"
                + "          ic_trans.trans_flag,\n"
                + "          ic_trans.doc_date,\n"
                + "          ic_trans.doc_no,\n"
                + "          ic_trans.send_type,"
                + "          ic_trans.inquiry_type as sale_type,\n"
                + "          ic_trans.cust_code,\n"
                + "          ic_trans.branch_code,\n"
                + "          COALESCE((select name_1 from erp_branch_list where code = branch_code),'') as branch_name,"
                + "          COALESCE((select name_1 from ar_customer where code = cust_code),'') as cust_name"
                + "   FROM ic_trans\n"
                + "   WHERE \n"
                + "     doc_date >= '" + __strStartDate + "'\n"
                + "     AND (is_hold = 0 OR (is_hold = 1 AND approve_status = 1))\n"
                + "     AND last_status=0\n"
                + "     AND pos_transfer = 0\n"
                + "           AND ((trans_flag = '44'\n"
                + "           AND inquiry_type IN (1,3)\n"
                + "           AND send_type=0\n"
                + "           AND NOT EXISTS\n"
                + "             (SELECT doc_no\n"
                + "              FROM pp_trans\n"
                + "              WHERE pp_trans.ref_code = ic_trans.doc_no))\n"
                + "          OR (trans_flag = '36'\n"
                + "              AND inquiry_type IN (0,\n"
                + "                                   2)\n"
                + "              AND send_type=0\n"
                + "              AND NOT EXISTS\n"
                + "                (SELECT doc_no\n"
                + "                 FROM pp_trans\n"
                + "                 WHERE pp_trans.ref_code = ic_trans.doc_no))\n"
                + "          )\n"
                + "     AND EXISTS\n"
                + "       (SELECT doc_no\n"
                + "        FROM ic_trans_detail\n"
                + "        WHERE ic_trans_detail.doc_no=ic_trans.doc_no\n"
                + "          AND ic_trans_detail.trans_flag=ic_trans.trans_flag\n"
                + "          " + __strWhCode + __strShelfCode + __strQueryExtends + " ) ) AS ic_trans\n"
                + " ORDER BY doc_date DESC,doc_no \n";*/
        String __strQUERY = "SELECT is_hold,\n"
                + "       trans_flag,\n"
                + "       doc_date,\n"
                + "       doc_no,\n"
                + "       send_type,\n"
                + "       sale_type,\n"
                + "       cust_code,\n"
                + "       branch_code,\n"
                + "       branch_name,\n"
                + "       cust_name  FROM\n"
                + "  (SELECT ic_trans.is_hold,\n"
                + "          ic_trans.trans_flag,\n"
                + "          ic_trans.doc_date,\n"
                + "          ic_trans.doc_no,\n"
                + "          ic_trans.send_type,\n"
                + "          ic_trans.inquiry_type as sale_type,\n"
                + "          ic_trans.cust_code,\n"
                + "          ic_trans.branch_code,\n"
                + "          COALESCE((select name_1 from erp_branch_list where code = branch_code),'') as branch_name,\n"
                + "          COALESCE((select name_1 from ar_customer where code = cust_code),'') as cust_name   FROM ic_trans\n"
                + "   WHERE \n"
                + "     doc_date >= '" + __strStartDate + "'\n"
                + "     AND last_status=0\n"
                + "     AND pos_transfer = 0 \n"
                + _transCondition
                + "           AND NOT EXISTS\n"
                + "             (SELECT doc_no\n"
                + "              FROM pp_trans\n"
                + "              WHERE pp_trans.ref_code = ic_trans.doc_no)\n"
                + "        \n"
                + "     AND EXISTS\n"
                + "       (SELECT doc_no\n"
                + "        FROM ic_trans_detail\n"
                + "        WHERE ic_trans_detail.doc_no=ic_trans.doc_no\n"
                + "          AND ic_trans_detail.trans_flag=ic_trans.trans_flag\n"
                + "          " + __strWhCode + __strShelfCode + __strQueryExtends + __refCode + "  ) ) AS ic_trans\n"
                + " ORDER BY doc_date DESC,doc_no ";
        conn.setAutoCommit(false);
        System.out.println("__strQUERY " + __strQUERY);
        __objTMP.put("row_count", __routine._getRowCount(conn, __strQUERY));
        String __strQueryPagination = " LIMIT " + __strLimit + " OFFSET " + __strOffset;
        PreparedStatement __stmt1;

        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY + __strQueryPagination, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();
        String __rsHTML = "";
        String[] __arrSendType = {"รับเอง", "ส่งให้"};
        String[] __arrSaleType = {"ขายเงินเชื่อ", "ขายเงินสด", "ขายสินค้าเงินสด (สินค้าบริการ)", "ขายสินค้าเงินเชื่อ (สินค้าบริการ)"};

        while (__rsData1.next()) {

            String __strDocumentDate = __rsData1.getString("doc_date").equals("") ? "-" : __rsData1.getString("doc_date");
            String __strDocNo = __rsData1.getString("doc_no").equals("") ? "-" : __rsData1.getString("doc_no");
            String __strCustomerCode = __rsData1.getString("cust_code").equals("") ? "-" : __rsData1.getString("cust_code");
            String __strCustomerName = __rsData1.getString("cust_name").equals("") ? "-" : __rsData1.getString("cust_name");
            String __strBranchCode = __rsData1.getString("branch_code").equals("") ? "-" : __rsData1.getString("branch_code");
            String __strBranchName = __rsData1.getString("branch_name").equals("") ? "-" : __rsData1.getString("branch_name");
            String __strTransFlag = __rsData1.getString("trans_flag").equals("") ? "-" : __rsData1.getString("trans_flag");
            /* Integer __strLastStatus = __rsData1.getInt("last_status");
            Integer __strFromDetail = __rsData1.getInt("from_detail");

            String __strBgColor;
            if (__strFromDetail == 0) {
                __strBgColor = "style='background-color: #F98180; color: #FFF' from_detail='" + "ic_trans" + "' ";
            } else if (__strFromDetail == 1 && __strLastStatus == 1) {
                __strBgColor = "style='background-color: #FFB54A; color: #FFF' from_detail='" + "pp_trans" + "' ";
            } else {
                __strBgColor = "from_detail='" + "pp_trans" + "' ";
            }*/
            String _docType = "";
            if (__strTransFlag.equals("44")) {
                _docType = "ขายสินค้า";
            } else if (__strTransFlag.equals("36")) {
                _docType = "ใบสั่งขาย";
            } else if (__strTransFlag.equals("34")) {
                _docType = "สั่งซื้อ/สั่งจอง";
            } else if (__strTransFlag.equals("30")) {
                _docType = "เสนอราคา";
            }

            JSONObject obj = new JSONObject();
            obj.put("trans_flag", __strTransFlag);
            obj.put("doc_no", __strDocNo);
            obj.put("doc_type", _docType);

            obj.put("doc_date", __routine._convertDate(__strDocumentDate));
            obj.put("branch_code", __strBranchCode);
            obj.put("branch_name", __strBranchName);
            obj.put("cust_code", __strCustomerCode);
            obj.put("cust_name", __strCustomerName);

            obj.put("sale_type", __arrSaleType[__rsData1.getInt("sale_type")]);

            obj.put("send_type", __arrSendType[__rsData1.getInt("send_type")]);

            /*if (__strFromDetail == 1 && __strLastStatus == 1) {
                __rsHTML += "<td><button type='button' id='btn-confirm-doc' class='btn btn-success btn-flat'>อนุมัติใบจัดอีกครั้ง</button></td>";
            } else {
                if (__strLastStatus == 1) {
                    __rsHTML += "<td><button type='button' id='btn-cancel-doc' class='btn btn-danger btn-flat' disabled='true'>ยกเลิกการอนุมัติ</button></td>";
                } else {
                    __rsHTML += "<td><button type='button' id='btn-cancel-doc' class='btn btn-danger btn-flat'>ยกเลิกการอนุมัติ</button></td>";
                }
            }*/
            __jsonArr.put(obj);
        }

        __stmt1.close();
        __rsData1.close();
        conn.commit();
        __objTMP.put("success", true);
        __objTMP.put("data", __jsonArr);

        return __objTMP;
    }

    private JSONObject _getSubDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");
//        String __strDocNo = !param.isNull("doc_no") && param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";

        String doc_no = "";
        doc_no = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";

        String __strQUERY = "select item_code,item_name,wh_code,line_number,coalesce(lot_number_1,'') as lot_number_1,COALESCE((select name_1 from ic_warehouse where code=wh_code), '') AS wh_name,shelf_code,COALESCE((select name_1 from ic_shelf where code=shelf_code and whcode = wh_code), '') AS shelf_name,qty from ic_trans_detail where doc_no = '" + doc_no + "'";

        conn.setAutoCommit(false);
        String __rsHTML = "";
        PreparedStatement __stmt1;
        ResultSet __rsData1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __rsData1 = __stmt1.executeQuery();

        __rsHTML += "<td colspan='13' style='padding: 5px 0'>";
        __rsHTML += "<div>";
        __rsHTML += "<table class='table table-hover' style='margin: 0;'>";
        __rsHTML += "<tr class='text-center' style='background-color: #F5B041; color: #F8F9F9'>";
        __rsHTML += "<td><strong>ลำดับ</strong></td>";
        __rsHTML += "<td><strong>รหัสสินค้า ~ ชื่อสินค้า</strong></td>";
        __rsHTML += "<td><strong>รหัสคลัง ~ ชื่อคลัง</strong></td>";
        __rsHTML += "<td><strong>รหัสที่เก็บ ~ ชื่อที่เก็บ</strong></td>";
        __rsHTML += "<td><strong>เลข LOT</strong></td>";
        __rsHTML += "<td><strong>จำนวน</strong></td>";
        __rsHTML += "</tr>";

        Integer __rowNumber = 0;
        String __strDetail = "";
        Boolean isPlus = false;
        while (__rsData1.next()) {
            if (__rowNumber == 0 && __rsData1.getInt("line_number") == 0) {
                isPlus = true;
            }
            __strDetail = "";
            String __bgColor = __rowNumber % 2 == 0 ? "#FEF9E7" : "#F9E79F";

            String[] __arrStatus = {"ปกติ", "ปิด", "ปิดไม่ปกติ"};
            String[] __arrBgStatus = {"#449D44", "#944DFF", "#F98180"};

            __strDetail += "<tr style='background-color: " + __bgColor + "' color: #000;>";
            __strDetail += "<td><h5><strong>" + (isPlus ? (__rsData1.getInt("line_number") + 1) : __rsData1.getInt("line_number")) + "</strong></h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("item_code") + " ~ " + __rsData1.getString("item_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("wh_code") + " ~ " + __rsData1.getString("wh_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("shelf_code") + " ~ " + __rsData1.getString("shelf_name") + "</h5></td>";
            __strDetail += "<td><h5>" + __rsData1.getString("lot_number_1") + "</h5></td>";
            __strDetail += "<td><h5>" + String.format("%,.2f", Float.parseFloat(__rsData1.getString("qty"))) + "</h5></td>";
            __rsHTML += __strDetail;
            __rsHTML += "</tr>";

            __rowNumber++;
        }
        if (__strDetail.equals("")) {
            __rsHTML += "<tr><td colspan='13'>ไม่พบข้อมูล</td></tr>";
        }

        __rsHTML += "</table>";
        __rsHTML += "</div>";
        __rsHTML += "</td>";

        __stmt1.close();
        __rsData1.close();

        __objTMP.put("success", true);
        __objTMP.put("data", __rsHTML);

        return __objTMP;
    }

    private JSONObject _approveDocDetail(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String doc_no = "";
        doc_no = !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? param.getString("doc_no") : "";
        String trans_flag = "";
        trans_flag = !param.isNull("trans_flag") && !param.getString("trans_flag").trim().isEmpty() ? param.getString("trans_flag") : "";
        String __strWhCode = !param.isNull("wh_code") && !param.getString("wh_code").trim().isEmpty() ? " AND (wh_code IN " + param.getString("wh_code") + ") " : "";
        String __strShelfCode = !param.isNull("shelf_code") && !param.getString("shelf_code").trim().isEmpty() ? " AND (shelf_code IN " + param.getString("shelf_code") + ") " : "";

        conn.setAutoCommit(false);

        String __strQUERY = "SELECT DISTINCT wh_code,shelf_code FROM ic_trans_detail WHERE doc_no='" + doc_no + "'" + __strWhCode + __strShelfCode + " ORDER BY wh_code";
        PreparedStatement __stmtGetWhCodeAndShelfCode;
        ResultSet __rsDataWhCodeAndShelfCode;
        __stmtGetWhCodeAndShelfCode = conn.prepareStatement(__strQUERY);
        __rsDataWhCodeAndShelfCode = __stmtGetWhCodeAndShelfCode.executeQuery();

        Integer __Line = 1;
        while (__rsDataWhCodeAndShelfCode.next()) {
            StringBuilder __strNewDocNo = new StringBuilder();
            UUID __strUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
            String[] __arrGUID = __strUUID.randomUUID().toString().split("-");
            __strNewDocNo.append(String.valueOf(__Line));
            __strNewDocNo.append(__strNewDocNo).append(__arrGUID[0]);
            String __strTmpDocNo = doc_no + '-';
            if (String.valueOf(__Line).length() == 1) {
                __strTmpDocNo += "0" + __Line;
            } else if (String.valueOf(__Line).length() == 2) {
                __strTmpDocNo += String.valueOf(__Line);
            }

            __strQUERY = "INSERT INTO pp_trans(trans_flag,doc_no,e_doc_no,b_doc_no,doc_date,ref_code,ref_date,due_date,cust_code,sale_code,status,remark,"
                    + " create_date_time_now,sale_type,send_type,department_code,branch_code,creator_code,create_date_time,confirm_code,total_amount,confirm_date,"
                    + " confirm_time,wh_code,shelf_code,confirm_date_time,lastedit_datetime) SELECT " + trans_flag + ",'" + __strTmpDocNo + "','E-" + __strNewDocNo + "'"
                    + " ,'B-" + __strNewDocNo + "',now(),doc_no,doc_date,due_date,cust_code,sale_code,0,remark,now(),inquiry_type,send_type,department_code,branch_code"
                    + " ,creator_code,create_datetime,'" + __strUserCode + "',total_amount,now(),now(),'" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "'"
                    + " ,'" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "',now(),lastedit_datetime FROM ic_trans WHERE doc_no = '" + doc_no + "'";

            PreparedStatement __stmtInsertPPTrans;
            System.out.println("__strQUERY " + __strQUERY);
            __stmtInsertPPTrans = conn.prepareStatement(__strQUERY);
            __stmtInsertPPTrans.executeUpdate();
            __stmtInsertPPTrans.close();

            __strQUERY = "INSERT INTO pp_trans_detail (doc_no,doc_date,ref_code,ref_date,ic_code,wh_code,shelf_code,unit_code,qty,create_date_time_now,department_code,branch_code,line_number,event_qty,sum_amount,price,lot_number_1) SELECT '" + __strTmpDocNo + "',now(),doc_no,doc_date,item_code,wh_code,shelf_code,unit_code,qty,now(),department_code,branch_code,line_number,qty,sum_amount,price,COALESCE(lot_number_1,'') FROM ic_trans_detail WHERE item_code <> '' and  item_code IS NOT NULL  and doc_no='" + doc_no + "' AND wh_code='" + __rsDataWhCodeAndShelfCode.getString("wh_code") + "' AND shelf_code='" + __rsDataWhCodeAndShelfCode.getString("shelf_code") + "' ";
            PreparedStatement __stmtInsertPPTransDetails;
            __stmtInsertPPTransDetails = conn.prepareStatement(__strQUERY);
            __stmtInsertPPTransDetails.executeUpdate();
            __stmtInsertPPTransDetails.close();

            __strQUERY = "UPDATE pp_trans SET total_amount = (SELECT SUM(sum_amount) FROM pp_trans_detail WHERE doc_no='" + __strNewDocNo + "') WHERE doc_no='" + __strNewDocNo + "'";

            PreparedStatement __stmtUpdateAmount;
            __stmtUpdateAmount = conn.prepareStatement(__strQUERY);
            __stmtUpdateAmount.executeUpdate();
            __stmtUpdateAmount.close();

            __Line++;
        }
        __rsDataWhCodeAndShelfCode.close();
        __rsDataWhCodeAndShelfCode.close();
        System.err.println(doc_no + ": send_type = 0");
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

    private JSONObject _reConfrim(Connection conn, JSONObject param) throws SQLException, Exception {
        JSONObject __objTMP = new JSONObject("{'success': false}");

        String __strQueryExtends = "";
        __strQueryExtends += !param.isNull("doc_no") && !param.getString("doc_no").trim().isEmpty() ? " AND (doc_no='" + param.getString("doc_no") + "') " : "";
        __strQueryExtends += !param.isNull("ref_code") && !param.getString("ref_code").trim().isEmpty() ? " AND (ref_code='" + param.getString("ref_code") + "') " : "";

        __strQueryExtends = __strQueryExtends.equals("") ? "" : " WHERE 1=1 " + __strQueryExtends;

        String __strQUERY = "UPDATE pp_trans SET status=3,last_status=0 " + __strQueryExtends;

        conn.setAutoCommit(false);
        PreparedStatement __stmt1;
        __stmt1 = conn.prepareStatement(__strQUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        __stmt1.executeUpdate();
        __stmt1.close();
        conn.commit();
        __objTMP.put("success", true);

        return __objTMP;
    }

}
