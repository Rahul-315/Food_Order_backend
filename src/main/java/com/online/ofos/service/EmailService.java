package com.online.ofos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async("taskExecutor")
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
       
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendWelcomeMail(String email, String username) {
        send(
            email,
            "Welcome to QuickBite 🍔",
            "Hello " + username + ",\n\n" +
            "Welcome to QuickBite!\n" +
            "Your account has been created successfully. 🎉 \n\n" +
            "Order fast. Eat happy.\n\n" +
            "– Team QuickBite"
        );
    }

    public void sendOrderConfirmation(String email, Long orderId) {
        send(
            email,
            "QuickBite | Order Confirmed",
            "Your order has been placed successfully! ✅ \n\n" +
            "Order ID: " + orderId + "\n\n" +
            "Thanks for choosing QuickBite 🍕"
        );
    }

    public void sendOrderStatusUpdate(String email, Long orderId, String status) {
        send(
            email,
            "QuickBite | Order Update",
            "Your order (ID: " + orderId + ") is now: " + status + ".\n\n" +
            "Thank you for ordering with QuickBite."
        );
    }

    public void sendRestaurantAddedMail(String email, String restaurantName) {
        send(
            email,
            "QuickBite | Restaurant Added 🏪",
            "Hello,\n\n" +
            "Your restaurant \"" + restaurantName + "\" has been successfully added to QuickBite.\n\n" +
            "You can now start receiving orders.\n\n" +
            "– Team QuickBite"
        );
    }

    public void sendRestaurantDeletedMail(String email, String restaurantName) {
        send(
            email,
            "QuickBite | Restaurant Removed ❌🏪",
            "Hello,\n\n" +
            "Your restaurant \"" + restaurantName + "\" has been removed from QuickBite.\n\n" +
            "If this was a mistake, please contact support.\n\n" +
            "– Team QuickBite"
        );
    }
    public void sendPasswordResetOtp(String email, String otp) {
        send(
            email,
            "QuickBite | Password Reset OTP 🔐",
            "Your OTP to reset password is:\n\n" +
            otp + "\n\n" +
            "This OTP is valid for 3 minutes.\n\n" +
            "If you didn’t request this, ignore this email.\n\n" +
            "– Team QuickBite"
        );
    }

}
