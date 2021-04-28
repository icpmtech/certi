/*
 * $Id: UserContractPK.java,v 1.4 2010/02/04 19:40:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/02/04 19:40:41 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * PK for UserContract
 *
 * @author pjfsilva
 */
@Embeddable
public class UserContractPK implements Serializable {

    @Column(name = "contract_id")
    private long idContract;

    @Column(name = "user_id")
    private long idUser;

    public UserContractPK() {
    }


    public UserContractPK(long idContract) {
        this.idContract = idContract;
    }

    public UserContractPK(long idContract, long idUser) {
        this.idContract = idContract;
        this.idUser = idUser;
    }

    public long getIdContract() {
        return idContract;
    }

    public void setIdContract(long idContract) {
        this.idContract = idContract;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserContractPK that = (UserContractPK) o;

        if (idContract != that.idContract) {
            return false;
        }
        if (idUser != that.idUser) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (int) (idContract ^ (idContract >>> 32));
        result = 31 * result + (int) (idUser ^ (idUser >>> 32));
        return result;
    }
}