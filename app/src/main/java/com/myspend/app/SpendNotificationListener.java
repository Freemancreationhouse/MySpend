package com.myspend.app;
import android.service.notification.*; import android.app.*; import java.util.regex.*;
public class SpendNotificationListener extends NotificationListenerService {
 private static final Pattern AMT=Pattern.compile("(?:₹|Rs\\.?|INR)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",Pattern.CASE_INSENSITIVE);
 public void onNotificationPosted(StatusBarNotification s){String pkg=s.getPackageName(); if(!(pkg.contains("phonepe")||pkg.contains("paytm")||pkg.contains("mobikwik")||pkg.contains("bhim")||pkg.contains("upi")||pkg.contains("bank"))) return; Notification n=s.getNotification(); CharSequence t=n.extras.getCharSequence(Notification.EXTRA_TEXT); if(t==null)return; String x=t.toString(); Matcher m=AMT.matcher(x); if(!m.find())return; double a;try{a=Double.parseDouble(m.group(1).replace(",",""));}catch(Exception e){return;} String low=x.toLowerCase(); String kind=(low.contains("received")||low.contains("credited"))?"income":"expense"; String cat=categorize(low); String ref=pkg+"|"+s.getPostTime()+"|"+a+"|"+x.hashCode(); new SpendDb(this).add(s.getPostTime(),a,kind,merchant(x),cat,pkg,ref); }
 private String categorize(String x){if(x.matches(".*(swiggy|zomato|restaurant|cafe|food).*"))return "Food";if(x.matches(".*(uber|ola|metro|fuel|petrol).*"))return "Travel";if(x.matches(".*(amazon|flipkart|shopping|store).*"))return "Shopping";if(x.matches(".*(electricity|recharge|bill|broadband).*"))return "Bills";return "Other";}
 private String merchant(String x){return x.length()>55?x.substring(0,55):x;}
}
