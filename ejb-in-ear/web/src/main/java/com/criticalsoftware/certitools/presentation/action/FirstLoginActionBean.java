package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.presentation.action.certitools.LoginRedirectActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * @author pjfsilva
 */
public class FirstLoginActionBean extends AbstractActionBean {
    @Override
    public void fillLookupFields() {
        // not needed, all handlers redirect to another action bean
    }

    @DefaultHandler
    public Resolution redirectToModule() {
        if (this.isUserInRole("user")){
            return new RedirectResolution(LoginRedirectActionBean.class);
        } else{
            // first login
            getContext().getRequest().getSession().invalidate();
            return new RedirectResolution(LoginRedirectActionBean.class);
        }
    }
}
