package com.example.afinal.Interfaces.User.Transaction.Api;

import android.util.Log;

import com.example.afinal.Interfaces.User.Transaction.Constant.AppInfo;
import com.example.afinal.Interfaces.User.Transaction.Helper.Helpers;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateOrder {
    private class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;

        private CreateOrderData(String amount) throws Exception {
            long appTime = new Date().getTime();
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = "Android_Demo";
            AppTime = String.valueOf(appTime);
            Amount = amount;
            AppTransId = Helpers.getAppTransId();
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
            Description = "Merchant pay for order #" + Helpers.getAppTransId();
            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId,
                    this.AppTransId,
                    this.AppUser,
                    this.Amount,
                    this.AppTime,
                    this.EmbedData,
                    this.Items);

            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);

            // Log request data
            Log.d("ZaloPay", "Request data:");
            Log.d("ZaloPay", "AppId: " + AppId);
            Log.d("ZaloPay", "AppUser: " + AppUser);
            Log.d("ZaloPay", "AppTime: " + AppTime);
            Log.d("ZaloPay", "Amount: " + Amount);
            Log.d("ZaloPay", "AppTransId: " + AppTransId);
            Log.d("ZaloPay", "EmbedData: " + EmbedData);
            Log.d("ZaloPay", "Items: " + Items);
            Log.d("ZaloPay", "BankCode: " + BankCode);
            Log.d("ZaloPay", "Description: " + Description);
            Log.d("ZaloPay", "Mac: " + Mac);
            Log.d("ZaloPay", "inputHMac: " + inputHMac);
        }
    }

    public JSONObject createOrder(String amount) throws Exception {
        CreateOrderData input = new CreateOrderData(amount);

        RequestBody formBody = new FormBody.Builder()
                .add("appid", input.AppId)
                .add("appuser", input.AppUser)
                .add("apptime", input.AppTime)
                .add("amount", input.Amount)
                .add("apptransid", input.AppTransId)
                .add("embeddata", input.EmbedData)
                .add("item", input.Items)
                .add("bankcode", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .build();

        JSONObject data = HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
        return data;
    }
}


