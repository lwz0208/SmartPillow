package entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Li Wenzhao on 2017/12/4.
 */

public class NewsData {

    /**
     * code : 200
     * status : ok
     * message :
     * data : {"Items":[{"newsId":9,"newsTitle":"心酸跳楼价","newsAbstract":"强势围观，你来不来","newsContent":"<p>强势围观，你来不来<\/p>","newsDate":"2017-11-22T00:00:00","newsPicture":"/ueditor/net/upload/image/20171129/6364758947196677996108778.png","Usex":2,"LoginPlace":"武汉市","StartAge":10,"EndAge":40,"StartIncome":1000,"EndIncome":20000,"Operator":"uu101","OpTime":"2017-11-29T21:58:25.66","newsType":"health_product","Reserve1":null,"Reserve2":null,"Reserve3":null,"Reserve4":null,"Reserve5":null}],"TotalCount":1,"PageCount":1,"PageSize":10,"Page":1}
     */

    private String code;
    private String status;
    private String message;
    /**
     * Items : [{"newsId":9,"newsTitle":"心酸跳楼价","newsAbstract":"强势围观，你来不来","newsContent":"<p>强势围观，你来不来<\/p>","newsDate":"2017-11-22T00:00:00","newsPicture":"/ueditor/net/upload/image/20171129/6364758947196677996108778.png","Usex":2,"LoginPlace":"武汉市","StartAge":10,"EndAge":40,"StartIncome":1000,"EndIncome":20000,"Operator":"uu101","OpTime":"2017-11-29T21:58:25.66","newsType":"health_product","Reserve1":null,"Reserve2":null,"Reserve3":null,"Reserve4":null,"Reserve5":null}]
     * TotalCount : 1
     * PageCount : 1
     * PageSize : 10
     * Page : 1
     */

    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int TotalCount;
        private int PageCount;
        private int PageSize;
        private int Page;
        /**
         * newsId : 9
         * newsTitle : 心酸跳楼价
         * newsAbstract : 强势围观，你来不来
         * newsContent : <p>强势围观，你来不来</p>
         * newsDate : 2017-11-22T00:00:00
         * newsPicture : /ueditor/net/upload/image/20171129/6364758947196677996108778.png
         * Usex : 2
         * LoginPlace : 武汉市
         * StartAge : 10
         * EndAge : 40
         * StartIncome : 1000
         * EndIncome : 20000
         * Operator : uu101
         * OpTime : 2017-11-29T21:58:25.66
         * newsType : health_product
         * Reserve1 : null
         * Reserve2 : null
         * Reserve3 : null
         * Reserve4 : null
         * Reserve5 : null
         */

        private List<ItemsBean> Items;

        public int getTotalCount() {
            return TotalCount;
        }

        public void setTotalCount(int TotalCount) {
            this.TotalCount = TotalCount;
        }

        public int getPageCount() {
            return PageCount;
        }

        public void setPageCount(int PageCount) {
            this.PageCount = PageCount;
        }

        public int getPageSize() {
            return PageSize;
        }

        public void setPageSize(int PageSize) {
            this.PageSize = PageSize;
        }

        public int getPage() {
            return Page;
        }

        public void setPage(int Page) {
            this.Page = Page;
        }

        public List<ItemsBean> getItems() {
            return Items;
        }

        public void setItems(List<ItemsBean> Items) {
            this.Items = Items;
        }

        public static class ItemsBean implements Serializable{
            private String newsId;
            private String newsTitle;
            private String newsAbstract;
            private String newsContent;
            private String newsDate;
            private String newsPicture;
            private int Usex;
            private String LoginPlace;
            private int StartAge;
            private int EndAge;
            private int StartIncome;
            private int EndIncome;
            private String Operator;
            private String OpTime;
            private String newsType;
            private Object Reserve1;
            private Object Reserve2;
            private Object Reserve3;
            private Object Reserve4;
            private Object Reserve5;

            public String getNewsId() {
                return newsId;
            }

            public void setNewsId(String newsId) {
                this.newsId = newsId;
            }

            public String getNewsTitle() {
                return newsTitle;
            }

            public void setNewsTitle(String newsTitle) {
                this.newsTitle = newsTitle;
            }

            public String getNewsAbstract() {
                return newsAbstract;
            }

            public void setNewsAbstract(String newsAbstract) {
                this.newsAbstract = newsAbstract;
            }

            public String getNewsContent() {
                return newsContent;
            }

            public void setNewsContent(String newsContent) {
                this.newsContent = newsContent;
            }

            public String getNewsDate() {
                return newsDate;
            }

            public void setNewsDate(String newsDate) {
                this.newsDate = newsDate;
            }

            public String getNewsPicture() {
                return newsPicture;
            }

            public void setNewsPicture(String newsPicture) {
                this.newsPicture = newsPicture;
            }

            public int getUsex() {
                return Usex;
            }

            public void setUsex(int Usex) {
                this.Usex = Usex;
            }

            public String getLoginPlace() {
                return LoginPlace;
            }

            public void setLoginPlace(String LoginPlace) {
                this.LoginPlace = LoginPlace;
            }

            public int getStartAge() {
                return StartAge;
            }

            public void setStartAge(int StartAge) {
                this.StartAge = StartAge;
            }

            public int getEndAge() {
                return EndAge;
            }

            public void setEndAge(int EndAge) {
                this.EndAge = EndAge;
            }

            public int getStartIncome() {
                return StartIncome;
            }

            public void setStartIncome(int StartIncome) {
                this.StartIncome = StartIncome;
            }

            public int getEndIncome() {
                return EndIncome;
            }

            public void setEndIncome(int EndIncome) {
                this.EndIncome = EndIncome;
            }

            public String getOperator() {
                return Operator;
            }

            public void setOperator(String Operator) {
                this.Operator = Operator;
            }

            public String getOpTime() {
                return OpTime;
            }

            public void setOpTime(String OpTime) {
                this.OpTime = OpTime;
            }

            public String getNewsType() {
                return newsType;
            }

            public void setNewsType(String newsType) {
                this.newsType = newsType;
            }

            public Object getReserve1() {
                return Reserve1;
            }

            public void setReserve1(Object Reserve1) {
                this.Reserve1 = Reserve1;
            }

            public Object getReserve2() {
                return Reserve2;
            }

            public void setReserve2(Object Reserve2) {
                this.Reserve2 = Reserve2;
            }

            public Object getReserve3() {
                return Reserve3;
            }

            public void setReserve3(Object Reserve3) {
                this.Reserve3 = Reserve3;
            }

            public Object getReserve4() {
                return Reserve4;
            }

            public void setReserve4(Object Reserve4) {
                this.Reserve4 = Reserve4;
            }

            public Object getReserve5() {
                return Reserve5;
            }

            public void setReserve5(Object Reserve5) {
                this.Reserve5 = Reserve5;
            }
        }
    }
}
