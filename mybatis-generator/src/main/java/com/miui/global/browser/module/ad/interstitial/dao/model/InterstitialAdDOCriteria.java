package com.miui.global.browser.module.ad.interstitial.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InterstitialAdDOCriteria {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public InterstitialAdDOCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("`id` is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("`id` is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("`id` =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("`id` <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("`id` >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("`id` >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("`id` <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("`id` <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("`id` in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("`id` not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("`id` between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("`id` not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("`type` is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("`type` is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("`type` =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("`type` <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("`type` >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("`type` >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("`type` <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("`type` <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("`type` in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("`type` not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("`type` between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("`type` not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andRegionIsNull() {
            addCriterion("`region` is null");
            return (Criteria) this;
        }

        public Criteria andRegionIsNotNull() {
            addCriterion("`region` is not null");
            return (Criteria) this;
        }

        public Criteria andRegionEqualTo(String value) {
            addCriterion("`region` =", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotEqualTo(String value) {
            addCriterion("`region` <>", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionGreaterThan(String value) {
            addCriterion("`region` >", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionGreaterThanOrEqualTo(String value) {
            addCriterion("`region` >=", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLessThan(String value) {
            addCriterion("`region` <", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLessThanOrEqualTo(String value) {
            addCriterion("`region` <=", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionLike(String value) {
            addCriterion("`region` like", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotLike(String value) {
            addCriterion("`region` not like", value, "region");
            return (Criteria) this;
        }

        public Criteria andRegionIn(List<String> values) {
            addCriterion("`region` in", values, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotIn(List<String> values) {
            addCriterion("`region` not in", values, "region");
            return (Criteria) this;
        }

        public Criteria andRegionBetween(String value1, String value2) {
            addCriterion("`region` between", value1, value2, "region");
            return (Criteria) this;
        }

        public Criteria andRegionNotBetween(String value1, String value2) {
            addCriterion("`region` not between", value1, value2, "region");
            return (Criteria) this;
        }

        public Criteria andAppVersionIsNull() {
            addCriterion("`app_version` is null");
            return (Criteria) this;
        }

        public Criteria andAppVersionIsNotNull() {
            addCriterion("`app_version` is not null");
            return (Criteria) this;
        }

        public Criteria andAppVersionEqualTo(String value) {
            addCriterion("`app_version` =", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionNotEqualTo(String value) {
            addCriterion("`app_version` <>", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionGreaterThan(String value) {
            addCriterion("`app_version` >", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionGreaterThanOrEqualTo(String value) {
            addCriterion("`app_version` >=", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionLessThan(String value) {
            addCriterion("`app_version` <", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionLessThanOrEqualTo(String value) {
            addCriterion("`app_version` <=", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionLike(String value) {
            addCriterion("`app_version` like", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionNotLike(String value) {
            addCriterion("`app_version` not like", value, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionIn(List<String> values) {
            addCriterion("`app_version` in", values, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionNotIn(List<String> values) {
            addCriterion("`app_version` not in", values, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionBetween(String value1, String value2) {
            addCriterion("`app_version` between", value1, value2, "appVersion");
            return (Criteria) this;
        }

        public Criteria andAppVersionNotBetween(String value1, String value2) {
            addCriterion("`app_version` not between", value1, value2, "appVersion");
            return (Criteria) this;
        }

        public Criteria andScaleIsNull() {
            addCriterion("`scale` is null");
            return (Criteria) this;
        }

        public Criteria andScaleIsNotNull() {
            addCriterion("`scale` is not null");
            return (Criteria) this;
        }

        public Criteria andScaleEqualTo(Integer value) {
            addCriterion("`scale` =", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleNotEqualTo(Integer value) {
            addCriterion("`scale` <>", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleGreaterThan(Integer value) {
            addCriterion("`scale` >", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleGreaterThanOrEqualTo(Integer value) {
            addCriterion("`scale` >=", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleLessThan(Integer value) {
            addCriterion("`scale` <", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleLessThanOrEqualTo(Integer value) {
            addCriterion("`scale` <=", value, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleIn(List<Integer> values) {
            addCriterion("`scale` in", values, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleNotIn(List<Integer> values) {
            addCriterion("`scale` not in", values, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleBetween(Integer value1, Integer value2) {
            addCriterion("`scale` between", value1, value2, "scale");
            return (Criteria) this;
        }

        public Criteria andScaleNotBetween(Integer value1, Integer value2) {
            addCriterion("`scale` not between", value1, value2, "scale");
            return (Criteria) this;
        }

        public Criteria andReloadIsNull() {
            addCriterion("`reload` is null");
            return (Criteria) this;
        }

        public Criteria andReloadIsNotNull() {
            addCriterion("`reload` is not null");
            return (Criteria) this;
        }

        public Criteria andReloadEqualTo(Integer value) {
            addCriterion("`reload` =", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadNotEqualTo(Integer value) {
            addCriterion("`reload` <>", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadGreaterThan(Integer value) {
            addCriterion("`reload` >", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadGreaterThanOrEqualTo(Integer value) {
            addCriterion("`reload` >=", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadLessThan(Integer value) {
            addCriterion("`reload` <", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadLessThanOrEqualTo(Integer value) {
            addCriterion("`reload` <=", value, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadIn(List<Integer> values) {
            addCriterion("`reload` in", values, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadNotIn(List<Integer> values) {
            addCriterion("`reload` not in", values, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadBetween(Integer value1, Integer value2) {
            addCriterion("`reload` between", value1, value2, "reload");
            return (Criteria) this;
        }

        public Criteria andReloadNotBetween(Integer value1, Integer value2) {
            addCriterion("`reload` not between", value1, value2, "reload");
            return (Criteria) this;
        }

        public Criteria andMaxNumberIsNull() {
            addCriterion("`max_number` is null");
            return (Criteria) this;
        }

        public Criteria andMaxNumberIsNotNull() {
            addCriterion("`max_number` is not null");
            return (Criteria) this;
        }

        public Criteria andMaxNumberEqualTo(Integer value) {
            addCriterion("`max_number` =", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberNotEqualTo(Integer value) {
            addCriterion("`max_number` <>", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberGreaterThan(Integer value) {
            addCriterion("`max_number` >", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberGreaterThanOrEqualTo(Integer value) {
            addCriterion("`max_number` >=", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberLessThan(Integer value) {
            addCriterion("`max_number` <", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberLessThanOrEqualTo(Integer value) {
            addCriterion("`max_number` <=", value, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberIn(List<Integer> values) {
            addCriterion("`max_number` in", values, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberNotIn(List<Integer> values) {
            addCriterion("`max_number` not in", values, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberBetween(Integer value1, Integer value2) {
            addCriterion("`max_number` between", value1, value2, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andMaxNumberNotBetween(Integer value1, Integer value2) {
            addCriterion("`max_number` not between", value1, value2, "maxNumber");
            return (Criteria) this;
        }

        public Criteria andEveryQueryIsNull() {
            addCriterion("`every_query` is null");
            return (Criteria) this;
        }

        public Criteria andEveryQueryIsNotNull() {
            addCriterion("`every_query` is not null");
            return (Criteria) this;
        }

        public Criteria andEveryQueryEqualTo(Integer value) {
            addCriterion("`every_query` =", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryNotEqualTo(Integer value) {
            addCriterion("`every_query` <>", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryGreaterThan(Integer value) {
            addCriterion("`every_query` >", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryGreaterThanOrEqualTo(Integer value) {
            addCriterion("`every_query` >=", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryLessThan(Integer value) {
            addCriterion("`every_query` <", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryLessThanOrEqualTo(Integer value) {
            addCriterion("`every_query` <=", value, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryIn(List<Integer> values) {
            addCriterion("`every_query` in", values, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryNotIn(List<Integer> values) {
            addCriterion("`every_query` not in", values, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryBetween(Integer value1, Integer value2) {
            addCriterion("`every_query` between", value1, value2, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryQueryNotBetween(Integer value1, Integer value2) {
            addCriterion("`every_query` not between", value1, value2, "everyQuery");
            return (Criteria) this;
        }

        public Criteria andEveryExposeIsNull() {
            addCriterion("`every_expose` is null");
            return (Criteria) this;
        }

        public Criteria andEveryExposeIsNotNull() {
            addCriterion("`every_expose` is not null");
            return (Criteria) this;
        }

        public Criteria andEveryExposeEqualTo(Integer value) {
            addCriterion("`every_expose` =", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeNotEqualTo(Integer value) {
            addCriterion("`every_expose` <>", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeGreaterThan(Integer value) {
            addCriterion("`every_expose` >", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeGreaterThanOrEqualTo(Integer value) {
            addCriterion("`every_expose` >=", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeLessThan(Integer value) {
            addCriterion("`every_expose` <", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeLessThanOrEqualTo(Integer value) {
            addCriterion("`every_expose` <=", value, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeIn(List<Integer> values) {
            addCriterion("`every_expose` in", values, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeNotIn(List<Integer> values) {
            addCriterion("`every_expose` not in", values, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeBetween(Integer value1, Integer value2) {
            addCriterion("`every_expose` between", value1, value2, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andEveryExposeNotBetween(Integer value1, Integer value2) {
            addCriterion("`every_expose` not between", value1, value2, "everyExpose");
            return (Criteria) this;
        }

        public Criteria andFirstShowIsNull() {
            addCriterion("`first_show` is null");
            return (Criteria) this;
        }

        public Criteria andFirstShowIsNotNull() {
            addCriterion("`first_show` is not null");
            return (Criteria) this;
        }

        public Criteria andFirstShowEqualTo(Integer value) {
            addCriterion("`first_show` =", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowNotEqualTo(Integer value) {
            addCriterion("`first_show` <>", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowGreaterThan(Integer value) {
            addCriterion("`first_show` >", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowGreaterThanOrEqualTo(Integer value) {
            addCriterion("`first_show` >=", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowLessThan(Integer value) {
            addCriterion("`first_show` <", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowLessThanOrEqualTo(Integer value) {
            addCriterion("`first_show` <=", value, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowIn(List<Integer> values) {
            addCriterion("`first_show` in", values, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowNotIn(List<Integer> values) {
            addCriterion("`first_show` not in", values, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowBetween(Integer value1, Integer value2) {
            addCriterion("`first_show` between", value1, value2, "firstShow");
            return (Criteria) this;
        }

        public Criteria andFirstShowNotBetween(Integer value1, Integer value2) {
            addCriterion("`first_show` not between", value1, value2, "firstShow");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("`create_time` is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("`create_time` is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("`create_time` =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("`create_time` <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("`create_time` >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("`create_time` >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("`create_time` <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("`create_time` <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("`create_time` in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("`create_time` not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("`create_time` between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("`create_time` not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("`update_time` is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("`update_time` is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("`update_time` =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("`update_time` <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("`update_time` >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("`update_time` >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("`update_time` <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("`update_time` <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("`update_time` in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("`update_time` not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("`update_time` between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("`update_time` not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}