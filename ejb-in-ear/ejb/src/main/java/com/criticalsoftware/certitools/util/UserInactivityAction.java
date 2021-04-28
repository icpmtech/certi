/*
 * $Id: UserInactivityAction.java,v 1.1 2010/01/14 17:09:55 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/01/14 17:09:55 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.entities.Contract;

/**
 * Description.
 *
 * @author :    Jo�o Gomes
 * @version :   $Revision: 1.1 $
 */

public class UserInactivityAction {

    public enum Type {
        SEND_FIRST_MESSAGE, SEND_SECOND_MESSAGE, DELETE_USER, REMOVE_CONTRACT_LICENSE
    }

    private Contract contract;
    private Type actionType;

    public UserInactivityAction(Contract contract, Type actionType) {
        this.contract = contract;
        this.actionType = actionType;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Type getActionType() {
        return actionType;
    }

    public void setActionType(Type actionType) {
        this.actionType = actionType;
    }
}



