package com.fengjixuan.push_info;

/**
 * Created by felix on 17-5-13.
 */

public class DemoBean {
    private String title ;
    private boolean canRemove = true ;

    public String getTitle() {
        return title ;
    }

    public void setTitle(String title) {
        this.title = title ;
    }

    public boolean isCanremove() {
        return canRemove ;
    }

    public void setCanremove(boolean canRemove) {
        this.canRemove = canRemove ;
    }

    public DemoBean(String title , boolean canRemove) {
        this.title = title ;
        this.canRemove = canRemove ;
    }

    public DemoBean() {

    }

}
