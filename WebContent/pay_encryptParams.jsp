<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ page import="net.sf.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@ page import="com.settle.pg.StringUtil"%>
<%@ page import="com.settle.pg.EncryptUtil"%>
<%@ include file="config.jsp" %>
<%
/** 로거 얻기 */
Logger logger = LoggerFactory.getLogger("trans");

/** 설정 정보 얻기 */
String licenseKey = LICENSE_KEY;
String aesKey = AES256_KEY;

/** 해쉬 및 aes256암호화 후 리턴 될 json */
JSONObject rsp = new JSONObject();

/** SHA256 해쉬 파라미터 */
String mchtId       = StringUtil.isNull(request.getParameter("mchtId"));
String method       = StringUtil.isNull(request.getParameter("method"));
String mchtTrdNo    = StringUtil.isNull(request.getParameter("mchtTrdNo"));
String trdDt        = StringUtil.isNull(request.getParameter("trdDt"));
String trdTm        = StringUtil.isNull(request.getParameter("trdTm"));
String trdAmt       = StringUtil.isNull(request.getParameter("plainTrdAmt"));

/** AES256 암호화 파라미터 */
HashMap<String,String> params = new HashMap<String, String>();
params.put("trdAmt",            trdAmt);
params.put("mchtCustNm",        StringUtil.isNull(request.getParameter("plainMchtCustNm")));
params.put("cphoneNo",          StringUtil.isNull(request.getParameter("plainCphoneNo")));
params.put("email",             StringUtil.isNull(request.getParameter("plainEmail")));
params.put("mchtCustId",        StringUtil.isNull(request.getParameter("plainMchtCustId")));
params.put("taxAmt",            StringUtil.isNull(request.getParameter("plainTaxAmt")));
params.put("vatAmt",            StringUtil.isNull(request.getParameter("plainVatAmt")));
params.put("taxFreeAmt",        StringUtil.isNull(request.getParameter("plainTaxFreeAmt")));
params.put("svcAmt",            StringUtil.isNull(request.getParameter("plainSvcAmt")));
params.put("clipCustNm",        StringUtil.isNull(request.getParameter("plainClipCustNm")));
params.put("clipCustCi",        StringUtil.isNull(request.getParameter("plainClipCustCi")));
params.put("clipCustPhoneNo",   StringUtil.isNull(request.getParameter("plainClipCustPhoneNo")));


/*============================================================================================================================================
 *  SHA256 해쉬 처리
 *조합 필드 : 상점아이디 + 결제수단 + 상점주문번호 + 요청일자 + 요청시간 + 거래금액(평문) + 라이센스키
 *============================================================================================================================================*/
String hashPlain = String.format("%s%s%s%s%s%s%s", mchtId, method, mchtTrdNo, trdDt, trdTm, trdAmt, licenseKey);
String hashCipher ="";
/** SHA256 해쉬 처리 */
try{
    hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
}catch(Exception e){
    logger.error("["+mchtTrdNo+"][SHA256 HASHING] Hashing Fail! : " + e.toString());
    throw e;
}finally{
    logger.info("["+mchtTrdNo+"][SHA256 HASHING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
    rsp.put("hashCipher", hashCipher); // sha256 해쉬 결과 저장
}

/*============================================================================================================================================
 *  AES256 암호화 처리(AES-256-ECB encrypt -> Base64 encoding)
 *============================================================================================================================================ */
try{
    for (Map.Entry<String, String> entry : params.entrySet()) {
        String key   = entry.getKey();
        String value =  entry.getValue();
        
        String aesPlain = params.get(key);
        if( !("".equals(aesPlain))){
            byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(aesKey, aesPlain);
            String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);
            
            params.put(key, aesCipher);//암호화된 데이터로 세팅
            logger.info("["+mchtTrdNo+"][AES256 Encrypt] "+key+"["+aesPlain+"] ---> ["+aesCipher+"]");
        }
    }

}catch(Exception e){
    logger.error("["+mchtTrdNo+"][AES256 Encrypt] AES256 Fail! : " + e.toString());
    throw e;
}finally{
    JSONObject encParams = JSONObject.fromObject(params); //aes256 암호화 결과 저장
    rsp.put("encParams", encParams);
}
/* 결과 리턴 */
out.println(rsp);













%>