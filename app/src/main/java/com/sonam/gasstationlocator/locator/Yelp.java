package com.sonam.gasstationlocator.locator;



/**
 * credit to Yelp  http://thysmichels.com/2011/12/30/yelp-api-example/
 * Created by dolmabradach on 4/7/15.
 */
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


/**
 * class for accessing the Yelp API.
 */


public class Yelp  {

    OAuthService service;
    Token accessToken;
    double lat;
    double lng;
    //String response;



    public Yelp( double latitude, double longitude) {
        String consumerKey = "DTQvwGy-vdFMqosjLv8aaQ";
        String consumerSecret = "GUlmPBvz8cow5p9vnN-yDKGA9-Y";
        String token = "CyTqYERoMsTsQjK4-rONq0cWLPWl2kVh";
        String tokenSecret = "U27751nqkSJGHioDPcdGN1F-uIg";

        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
        this.lat = latitude;
        this.lng= longitude;
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(String term, double latitude, double longitude) {

        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");

        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("cll", latitude + "," + longitude);
        request.addQuerystringParameter("limit", String.valueOf(10));
        this.service.signRequest(this.accessToken, request);

        Response response = request.send();
        return response.getBody();
    }

    // CLI
   // public  void main(String[] args) {
        // Update tokens here from Yelp developers site, Manage API access.


//        Yelp yelp = new Yelp( lat, lng);
     //  response = yelp.search("gas station", lat, lng);





      //  System.out.println(response);
  //  }
//    public String getResponse(){
//
//
//
//        return response;
//
//    }

}




