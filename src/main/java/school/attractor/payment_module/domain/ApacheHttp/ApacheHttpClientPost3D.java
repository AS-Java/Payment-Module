package school.attractor.payment_module.domain.ApacheHttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApacheHttpClientPost3D {

    public List<String> secureData = new ArrayList<> (  );
    public String responseCode="";
    public String errorMessage ="";


    public void sendRequest(String card, String exp, String expYear, String cvc2) throws IOException {

        try {
            final CloseableHttpClient httpclient = HttpClients.createDefault ( );
            final HttpPost httpPost = new HttpPost ( "https://test-ecom.atfbank.kz:5443/cgi-bin/cgi_link" );
            final List<NameValuePair> params = new ArrayList<> ( );
            params.add ( new BasicNameValuePair ( "CARD", card ) );
            params.add ( new BasicNameValuePair ( "EXP", exp ) );
            params.add ( new BasicNameValuePair ( "EXP_YEAR", expYear ) );
            params.add ( new BasicNameValuePair ( "CVC2", cvc2 ) );
            params.add ( new BasicNameValuePair ( "CVC2_RC", "1" ) );
            params.add ( new BasicNameValuePair ( "AMOUNT", "3" ) );
            params.add ( new BasicNameValuePair ( "CURRENCY", "840" ) );
            params.add ( new BasicNameValuePair ( "DESC", "Merchant_test" ) );
            params.add ( new BasicNameValuePair ( "MERCHANT", "ECOMM002" ) ); //Merchant ID assigned by bank
            params.add ( new BasicNameValuePair ( "TERMINAL", "ECOMM002" ) );//Merchant Terminal ID assigned by bank
            params.add ( new BasicNameValuePair ( "TRTYPE", "1" ) );//Must be equal to 1;
            params.add ( new BasicNameValuePair ( "EMAIL", "test@atfbank.kz" ) );
            params.add ( new BasicNameValuePair ( "MERCH_GMT", "+6" ) );
            params.add ( new BasicNameValuePair ( "MERCH_URL", "http://test.atfbank.kz" ) );//Merchant primary web site URL
            params.add ( new BasicNameValuePair ( "COUNTRY", "KZ" ) );
            try {
                httpPost.setEntity ( new UrlEncodedFormEntity ( params ) );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace ( );
            }

            try (CloseableHttpResponse response = httpclient.execute ( httpPost )) {
                final HttpEntity entity = response.getEntity ( );
                String htmlString = EntityUtils.toString ( entity );
                Document html = Jsoup.parse ( htmlString );
                System.out.println ( "html = " + html );
                if(html.title ().equals ( "Transaction declined" )){
                    String code = htmlString.split ( "(\\bRC Code\\b)" )[1].split ( "\\bp\\b" )[0].replaceAll ( "[^\\w \\xC0-\\xFF]","" ).trim ();
                    String rcCode = "-" + code;
                    System.out.println ("Transaction declined, Response Code " + rcCode );
                    String errMessage = htmlString.split ( "(\\bError message\\b)" )[1].split ( "\\bmsg\\b" )[0].split ("\\bp\\b" )[0].replaceAll ( "[^\\w \\xC0-\\xFF]","" ).trim ();
                    System.out.println ( "substring = " + errMessage );
                    responseCode = rcCode;
                    errorMessage = errMessage;
                } else{
                    String action = html.body ( ).getAllElements ( ).forms ( ).get ( 0 ).attributes ( ).get ( "ACTION" );
                    String PaReg = html.body ( ).getAllElements ( ).forms ( ).get ( 0 ).elements ( ).get ( 0 ).attributes ( ).get ( "value" );
                    String TermUrl = html.body ( ).getAllElements ( ).forms ( ).get ( 0 ).elements ( ).get ( 1 ).attributes ( ).get ( "value" );
                    String MD = html.body ( ).getAllElements ( ).forms ( ).get ( 0 ).elements ( ).get ( 2 ).attributes ( ).get ( "value" );
                    secureData.add ( action);
                    secureData.add(PaReg);
                    secureData.add (TermUrl);
                    secureData.add(MD );
                    System.out.println ("secure data: " + secureData );
                }
            } catch (IOException e) {
                e.printStackTrace ( );
            }
        } catch (ParseException e) {
            e.printStackTrace ( );
        }

    }

    public void post3DSecure(String action, String PaReg, String TermUrl, String MD) throws IOException {
        try {
            final CloseableHttpClient httpclient = HttpClients.createDefault ( );
            final HttpPost httpPost = new HttpPost ( action );
            final List<NameValuePair> params = new ArrayList<> ( );
            params.add ( new BasicNameValuePair ( "PaReq", PaReg ) );
            params.add ( new BasicNameValuePair ( "TermUrl", TermUrl ) );
            params.add ( new BasicNameValuePair ( "MD", MD ) );
            httpPost.setEntity ( new UrlEncodedFormEntity ( params ) );
            CloseableHttpResponse response = httpclient.execute ( httpPost );
            final HttpEntity entity = response.getEntity ( );
            String htmlString = EntityUtils.toString ( entity );
            Document html = Jsoup.parse ( htmlString );
            String PaRes = html.body().getAllElements ().forms ().get ( 0 ).elements().get ( 2 ).attributes().get ( "VALUE" );
//            post3DSecureToATF ( TermUrl, PaRes, MD );
        } catch (IOException e) {
            e.printStackTrace ( );
        }


    }

    public String post3DSecureToATF(String termUrl, String paRes, String md) {
        try {
            final CloseableHttpClient httpclient = HttpClients.createDefault ( );
            final HttpPost httpPost = new HttpPost ( termUrl );
            final List<NameValuePair> params = new ArrayList<> ( );
            params.add ( new BasicNameValuePair ( "PaRes", paRes ) );
            params.add ( new BasicNameValuePair ( "MD", md ) );
            httpPost.setEntity ( new UrlEncodedFormEntity ( params ) );
            CloseableHttpResponse response = httpclient.execute ( httpPost );
            final HttpEntity entity = response.getEntity ( );
            String htmlString = EntityUtils.toString ( entity );
            String[] substring = htmlString.split ( "(\\bCard number\\b)" )[1].split ( "\\bmsg\\b" );
            String cardNumber = substring[0].split("\\btd\\b")[2].replaceAll("[^\\w \\xC0-\\xFF]","" ).trim();
            String cardExpiry = substring[1].substring ( 45,50 );
            String trAm = substring[2].split("\\btd\\b")[3].replaceAll ( "[^\\w \\xC0-\\xFF]","" ).trim ();
            String trCcy =substring[3].substring ( 45,48 );
            String merchantID = substring[4].split ( "\\btd\\b" )[3].replaceAll ( "[^\\w \\xC0-\\xFF]","" ).trim();
            String transRef = substring[5].split("\\btd\\b")[3].replaceAll ( "[^\\w \\xC0-\\xFF]","").trim ();
            String intTrRef = substring[6].split("\\btd\\b")[3].replaceAll ( "[^\\w \\xC0-\\xFF]","").trim ();
            String apprCode = substring[7].split("\\btd\\b")[3].replaceAll ( "[^\\w \\xC0-\\xFF]","").trim ();
            String rcCode = substring[8].split("\\btd\\b")[3].replaceAll ( "[^\\w \\xC0-\\xFF]","").trim ();
            System.out.println ( "whole message from the Bank = " + htmlString );
            System.out.println ( "Card number = " + cardNumber);
            System.out.println ( "Card expiry = " + cardExpiry );
            System.out.println ( "Transaction amount = " + trAm );
            System.out.println ( "Transaction currency = " + trCcy );
            System.out.println ( "Merchant ID = " + merchantID );
            System.out.println ( "Transaction reference number = " + transRef );
            System.out.println ( "Internal Transaction number = " +intTrRef );
            System.out.println ( "Bank's approval code = " +apprCode );
            System.out.println ( "RC CODE = " +rcCode );
            responseCode = rcCode;
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .cardNumber ( cardNumber )
                    .cardExpiry ( cardExpiry )
                    .transactionAmount ( Double.parseDouble (trAm)/100 )
                    .transactionCcy ( trCcy )
                    .merchantId ( merchantID )
                    .transactionReference ( transRef )
                    .internalTransReference ( intTrRef )
                    .approvalCode ( apprCode )
                    .rcCode ( rcCode )
                    .build();
            System.out.println ("responseDTO: " + responseDTO );
            return rcCode;
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        return "no code";
    }
    }