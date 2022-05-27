package aliyunRAMTools.model;

import org.springframework.util.Assert;

/**
 * 类Page.java的实现描述：翻页相关
 *
 * @author charles 2015年9月23日 下午3:53:12
 */
public class Page {

    private Integer start;
    private Integer limit;
    private Integer total;

    private String sort;
    private String dir;

    public Page(Integer start, Integer limit) {
        super();
        Assert.notNull(start, "start can not be null!");
        Assert.notNull(limit, "limit can not be null!");
        this.start = start;
        this.limit = limit;
    }

    public Page(Integer start, Integer limit, String sort, String dir) {
        super();
        Assert.notNull(start, "start can not be null!");
        Assert.notNull(limit, "limit can not be null!");
        Assert.hasText(sort, "sort can not be null!");
        Assert.hasText(dir, "dir can not be null!");
        this.start = start;
        this.limit = limit;
        this.sort = sort;
        this.dir = dir;
    }

    @Override
    public String toString() {
        return "Page [start=" + start + ", limit=" + limit + ", total=" + total + ", sort=" + sort + ", dir=" + dir
                + "]";
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
