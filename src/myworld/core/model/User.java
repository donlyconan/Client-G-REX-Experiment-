package myworld.core.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("masv")
    private int masv;
    @SerializedName("url_avatar")
    private String avatar;
    @SerializedName("hoten")
    private String hoten;
    @SerializedName("malop")
    private String malop;
    @SerializedName("gioitinh")
    private int gioitinh;
    @SerializedName("sdt")
    private String sodt;
    @SerializedName("email")
    private String email;
    @SerializedName("diachi")
    private String diachi;

    public User() {
    }

    public User(int masv, String avatar, String hoten, String malop, int gioitinh, String sodt, String email, String diachi) {
        this.masv = masv;
        this.avatar = avatar;
        this.hoten = hoten;
        this.malop = malop;
        this.gioitinh = gioitinh;
        this.sodt = sodt;
        this.email = email;
        this.diachi = diachi;
    }

    public String toJsonString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "masv='" + masv + '\'' +
                ", avatar='" + avatar + '\'' +
                ", hoten='" + hoten + '\'' +
                ", malop='" + malop + '\'' +
                ", gioitinh=" + gioitinh +
                ", sodt='" + sodt + '\'' +
                ", email='" + email + '\'' +
                ", diachi='" + diachi + '\'' +
                '}';
    }

    public int getMasv() {
        return masv;
    }

    public void setMasv(int masv) {
        this.masv = masv;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getMalop() {
        return malop;
    }

    public void setMalop(String malop) {
        this.malop = malop;
    }

    public int getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(int gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getSodt() {
        return sodt;
    }

    public void setSodt(String sodt) {
        this.sodt = sodt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }
}
