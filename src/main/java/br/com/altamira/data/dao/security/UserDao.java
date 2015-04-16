/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.security;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.security.AccessToken;
import br.com.altamira.data.model.security.AccessToken_;
import br.com.altamira.data.model.security.User;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.security.UserDao")
public class UserDao extends BaseDao<User> {

    public User getUserByToken(String token) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = cb.createQuery(User.class);

        Root<AccessToken> accessToken = criteriaQuery.from(AccessToken.class);
        criteriaQuery.select(accessToken.get(AccessToken_.user));
        criteriaQuery.where(cb.equal(accessToken.get(AccessToken_.accessToken), token));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }    
}
