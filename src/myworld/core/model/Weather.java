package myworld.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    @SerializedName("title")
    private String title;
    @SerializedName("info")
    private List<WeatherInfo> info;

    public Weather() {
    }

    public Weather(String title, List<WeatherInfo> info) {
        this.title = title;
        this.info = info;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "title='" + title + '\'' +
                ", info=" + info +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WeatherInfo> getInfo() {
        return info;
    }

    public void setInfo(List<WeatherInfo> info) {
        this.info = info;
    }

    public static class WeatherInfo {
        //KEY_JSON_WEATHER = ['thoigian', 'dubao', 'nhietdo', 'mua', 'khiap', 'gio']
        @SerializedName("thoigian")
        protected String thoigian;
        @SerializedName("dubao")
        protected String dubao;
        @SerializedName("nhietdo")
        protected String nhietdo;
        @SerializedName("mua")
        protected String mua;
        @SerializedName("khiap")
        protected String khiap;
        @SerializedName("gio")
        protected String gio;

        public WeatherInfo() {
        }

        public WeatherInfo(String thoigian, String dubao, String nhietdo, String mua, String khiap, String gio) {
            this.thoigian = thoigian;
            this.dubao = dubao;
            this.nhietdo = nhietdo;
            this.mua = mua;
            this.khiap = khiap;
            this.gio = gio;
        }

        @Override
        public String toString() {
            return "WeatherInfo{" +
                    "thoigian='" + thoigian + '\'' +
                    ", dubao='" + dubao + '\'' +
                    ", nhietdo='" + nhietdo + '\'' +
                    ", mua='" + mua + '\'' +
                    ", khiap='" + khiap + '\'' +
                    ", gio='" + gio + '\'' +
                    '}';
        }

        public String getThoigian() {
            return thoigian;
        }

        public void setThoigian(String thoigian) {
            this.thoigian = thoigian;
        }

        public String getDubao() {
            return dubao;
        }

        public void setDubao(String dubao) {
            this.dubao = dubao;
        }

        public String getNhietdo() {
            return nhietdo;
        }

        public void setNhietdo(String nhietdo) {
            this.nhietdo = nhietdo;
        }

        public String getMua() {
            return mua;
        }

        public void setMua(String mua) {
            this.mua = mua;
        }

        public String getKhiap() {
            return khiap;
        }

        public void setKhiap(String khiap) {
            this.khiap = khiap;
        }

        public String getGio() {
            return gio;
        }

        public void setGio(String gio) {
            this.gio = gio;
        }
    }
}

