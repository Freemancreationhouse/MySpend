package com.myspend.app;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentNotificationListener extends NotificationListenerService {
    private static final Pattern AMOUNT = Pattern.compile("(?i)(?:₹|rs\\.?|inr)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)");
    private static final Pattern UPI = Pattern.compile("(?i)(?:upi(?:\\s*(?:ref|txn|transaction|id))?[:#\\s-]*)([A-Za-z0-9-]{6,})");

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences sp=getSharedPreferences("prefs",MODE_PRIVATE);
        if(!sp.getBoolean("auto_tracking",false)) return;
        String pkg=sbn.getPackageName();
        if(!isAllowed(pkg)) return;
        Notification n=sbn.getNotification(); if(n==null) return;
        Bundle e=n.extras;
        String title=String.valueOf(e.getCharSequence(Notification.EXTRA_TITLE,""));
        String text=String.valueOf(e.getCharSequence(Notification.EXTRA_TEXT,""));
        String big=String.valueOf(e.getCharSequence(Notification.EXTRA_BIG_TEXT,""));
        String all=(title+" "+text+" "+big).replace('\n',' ').trim();
        if(!looksFinancial(all)) return;
        Matcher am=AMOUNT.matcher(all); if(!am.find()) return;
        double amount; try{amount=Double.parseDouble(am.group(1).replace(",",""));}catch(Exception ex){return;}
        if(amount<=0) return;
        String low=all.toLowerCase(Locale.ROOT);
        String kind=(low.contains("credited")||low.contains("received")||low.contains("refund")||low.contains("cashback"))?"income":"expense";
        if(!(low.contains("paid")||low.contains("debited")||low.contains("sent")||low.contains("spent")||low.contains("purchase")||low.contains("credited")||low.contains("received")||low.contains("refund")||low.contains("cashback")||low.contains("transaction"))) return;
        String merchant=cleanMerchant(title,text,pkg);
        String cat=category(all);
        Matcher um=UPI.matcher(all);
        String ref=um.find()?"upi|"+um.group(1):"notif|"+sha(pkg+"|"+sbn.getPostTime()+"|"+amount+"|"+all);
        new SpendDb(this).add(sbn.getPostTime(),amount,kind,merchant,cat,"notification:"+pkg,ref);
    }

    private boolean isAllowed(String p){
        return p.equals("com.phonepe.app") || p.equals("net.one97.paytm") || p.equals("in.org.npci.upiapp") || p.equals("com.mobikwik_new") || p.equals("com.google.android.apps.nbu.paisa.user");
    }
    private boolean looksFinancial(String s){String x=s.toLowerCase(Locale.ROOT);return x.contains("₹")||x.contains("inr")||x.contains("rs.")||x.contains(" rs ");}
    private String cleanMerchant(String title,String text,String pkg){
        String s=(title==null||title.trim().isEmpty())?text:title; if(s==null||s.trim().isEmpty())s=appName(pkg);
        s=s.replaceAll("(?i)payment successful|transaction successful|paid successfully|money sent|payment received"," ").trim();
        return s.length()>42?s.substring(0,42):s;
    }
    private String appName(String p){if(p.contains("phonepe"))return "PhonePe";if(p.contains("paytm"))return "Paytm";if(p.contains("npci"))return "BHIM";if(p.contains("mobikwik"))return "MobiKwik";if(p.contains("paisa"))return "Google Pay";return "UPI transaction";}
    private String category(String s){String x=s.toLowerCase(Locale.ROOT);if(x.matches(".*(swiggy|zomato|restaurant|food|cafe|coffee).*"))return "Food";if(x.matches(".*(amazon|flipkart|shopping|store|mart).*"))return "Shopping";if(x.matches(".*(uber|ola|metro|rail|irctc|fuel|petrol|travel).*"))return "Travel";if(x.matches(".*(electric|recharge|broadband|bill|utility|gas).*"))return "Bills";return "Other";}
    private String sha(String s){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format("%02x",q));return x.toString();}catch(Exception e){return String.valueOf(s.hashCode());}}
}
