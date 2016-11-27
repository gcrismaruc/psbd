package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Gheorghe on 11/26/2016.
 */
@Controller
public class MainController {

    @RequestMapping(value = {"/", "/Index", "/Index.html", "/index"})
    public ModelAndView handle(){

        return new ModelAndView("Index");
    }

    @RequestMapping(value = {"/Booking", "/Booking.html"})
    public ModelAndView handleBooking(){

        return new ModelAndView("Booking");
    }

    @RequestMapping(value = {"/CancelBooking", "/CancelBooking.html"})
    public ModelAndView handleCancelBooking(){

        return new ModelAndView("CancelBooking");
    }

    @RequestMapping(value = {"/Payment", "/Payment.html"})
    public ModelAndView handlePayment(){

        return new ModelAndView("Payment");
    }
}
