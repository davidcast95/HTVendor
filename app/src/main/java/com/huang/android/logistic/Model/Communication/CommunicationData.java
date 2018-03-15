package com.huang.android.logistic.Model.Communication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidwibisono on 2/5/18.
 */

public class CommunicationData {
    @SerializedName("sender")
    public String sender;
    @SerializedName("sender_full_name")
    public String sender_full_name;
    @SerializedName("subject")
    public String subject;
    @SerializedName("content")
    public String content;
    @SerializedName("creation")
    public String creation;
    @SerializedName("reference_doctype")
    public String reference_doctype;
    @SerializedName("reference_name")
    public String reference_name;
    @SerializedName("communication_type")
    public String communication_type;
    @SerializedName("comment_type")
    public String comment_type;
}
