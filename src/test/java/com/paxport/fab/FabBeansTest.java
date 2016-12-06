package com.paxport.fab;

import com.paxport.fab.beans.Basket;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by ajchesney on 06/12/2016.
 */
public class FabBeansTest {

    private FabBeans beans = new FabBeans();

    @Test
    public void fromXMLTest(){

        InputStream xml = FabBeansTest.class.getResourceAsStream("/basket1.xml");

        Basket basket = beans.fromXML(xml,Basket.class);

        Assert.assertNotNull(basket);

        Assert.assertEquals("si3993",basket.getItinerary().getItineraryId());
    }

    @Test
    public void fromJSONTest(){

        InputStream xml = FabBeansTest.class.getResourceAsStream("/basket1.json");

        Basket basket = beans.fromJSON(xml,Basket.class);

        Assert.assertNotNull(basket);
        Assert.assertNotNull(basket.getItinerary());
        Assert.assertEquals("si3993",basket.getItinerary().getItineraryId());
    }


    @Test
    public void xmlToJsonTest(){

        InputStream xml = FabBeansTest.class.getResourceAsStream("/basket1.xml");

        String json = beans.xmlToJson(xml,Basket.class);

        System.out.println (json);

        Basket basket = beans.fromJSON(json,Basket.class);

        Assert.assertNotNull(basket);

        Assert.assertEquals("si3993",basket.getItinerary().getItineraryId());
    }

}
