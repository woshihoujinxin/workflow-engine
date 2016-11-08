package com.workflow.engine.core.service.entity.vo;

import java.io.Serializable;
import java.util.List;

public class WangXiaoResultVO implements Serializable {

    private static final long serialVersionUID = 5800320318673904235L;

    private String code = "0";
    private String message = "查询成功";
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data implements Serializable {
        private static final long serialVersionUID = 5750574657931077699L;
        public boolean success;  //报价是否完成
        public String code;
        public List<Quote> quotes;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<Quote> getQuotes() {
            return quotes;
        }

        public void setQuotes(List<Quote> quotes) {
            this.quotes = quotes;
        }

        public static class Quote implements Serializable {
            private static final long serialVersionUID = 4227158889382702188L;
            private int code = 1000;//报价返回码
            private String msg;//报价返回信息
            private String supplierCode;  //供应商代码
            private String supplierName; //供应商名
            private InsType insType;
            private Object stepInfo;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getSupplierCode() {
                return supplierCode;
            }

            public void setSupplierCode(String supplierCode) {
                this.supplierCode = supplierCode;
            }

            public String getSupplierName() {
                return supplierName;
            }

            public void setSupplierName(String supplierName) {
                this.supplierName = supplierName;
            }

            public InsType getInsType() {
                return insType;
            }

            public void setInsType(InsType insType) {
                this.insType = insType;
            }

            public Object getStepInfo() {
                return stepInfo;
            }

            public void setStepInfo(Object stepInfo) {
                this.stepInfo = stepInfo;
            }

            public static class InsType implements Serializable {

                private static final long serialVersionUID = 9111685426706116429L;
                public BizInfo bizInfo;

                public BizInfo getBizInfo() {
                    return bizInfo;
                }

                public void setBizInfo(BizInfo bizInfo) {
                    this.bizInfo = bizInfo;
                }

                public static class BizInfo implements Serializable {
                    private static final long serialVersionUID = 3011086462574651765L;
                    private boolean isSuccess;
                    String message; //商业险报价失败返回的提示明确的信息
                    String startDate;  //商业险起始日期
                    String total;  //商业险保费总计
                    public List<Detail> detail;

                    public boolean isSuccess() {
                        return isSuccess;
                    }

                    public void setSuccess(boolean success) {
                        this.isSuccess = success;
                    }

                    public String getMessage() {
                        return message;
                    }

                    public void setMessage(String message) {
                        this.message = message;
                    }

                    public String getStartDate() {
                        return startDate;
                    }

                    public void setStartDate(String startDate) {
                        this.startDate = startDate;
                    }

                    public String getTotal() {
                        return total;
                    }

                    public void setTotal(String total) {
                        this.total = total;
                    }

                    public List<Detail> getDetail() {
                        return detail;
                    }

                    public void setDetail(List<Detail> detail) {
                        this.detail = detail;
                    }

                    public static class Detail implements Serializable {
                        private static final long serialVersionUID = -6878447951331754339L;
                        String code; //险种代码
                        String name; //险种名称
                        String amount; //险种金额
                        String premium; //保费
                        String original;  //原价

                        public String getCode() {
                            return code;
                        }

                        public void setCode(String code) {
                            this.code = code;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getAmount() {
                            return amount;
                        }

                        public void setAmount(String amount) {
                            this.amount = amount;
                        }

                        public String getPremium() {
                            return premium;
                        }

                        public void setPremium(String premium) {
                            this.premium = premium;
                        }

                        public String getOriginal() {
                            return original;
                        }

                        public void setOriginal(String original) {
                            this.original = original;
                        }

                    }
                }

                public Force forceInfo;

                public Force getForceInfo() {
                    return forceInfo;
                }

                public void setForceInfo(Force forceInfo) {
                    this.forceInfo = forceInfo;
                }

                public static class Force implements Serializable {
                    private static final long serialVersionUID = -7677525339025406932L;
                    boolean isSuccess;
                    String message; //交强险报价失败返回的提示明确的信息
                    String startDate;  //起始日期
                    String tax; //车船税
                    String premium; //交强保费

                    public boolean isSuccess() {
                        return isSuccess;
                    }

                    public void setSuccess(boolean success) {
                        this.isSuccess = success;
                    }

                    public String getMessage() {
                        return message;
                    }

                    public void setMessage(String message) {
                        this.message = message;
                    }

                    public String getStartDate() {
                        return startDate;
                    }

                    public void setStartDate(String startDate) {
                        this.startDate = startDate;
                    }

                    public String getTax() {
                        return tax;
                    }

                    public void setTax(String tax) {
                        this.tax = tax;
                    }

                    public String getPremium() {
                        return premium;
                    }

                    public void setPremium(String premium) {
                        this.premium = premium;
                    }

                }
            }
        }
    }

}
