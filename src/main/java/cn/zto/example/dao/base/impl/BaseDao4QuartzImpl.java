package cn.zto.example.dao.base.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.zto.example.dao.base.BaseDao4Quartz;

@Repository("baseDao")
public class BaseDao4QuartzImpl implements BaseDao4Quartz{
	@Resource(name = "sf_quartz")
	private SessionFactory sessionFactory;

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到
		return sessionFactory.getCurrentSession();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void saveObject(Object o) {
		getSession().save(o);
	}

	public long create(Object o) {
		Long i = (Long) getSession().save(o);
		return i;
	}
	public void patchSave(List list) {
		
		for (int i = 0; i < list.size(); i++) {
			if (null!=list.get(i)) {
				 this.getSession().save(list.get(i));
				 if(i%100==0){
					 this.getSession().flush();
					 this.getSession().clear();
				 }
			}
			
		}
		
	}
	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();

	}

	public <T> void mergModify(T entity) {
		getSession().merge(entity);

	}

	@SuppressWarnings("rawtypes")
	public Object getObject(Class clazz, Serializable id) {
		return getSession().get(clazz, id);
	}

	@SuppressWarnings("rawtypes")
	public List<?> getObjects(Class clazz) {
		return getSession().createCriteria(clazz).list();
	}

	public List<?> find(String queryString) {
		Query query = getSession().createQuery(queryString.toString());
		return query.list();
	}

	public List<?> find(String queryString, Object value) {
		Query query = getSession().createQuery(queryString.toString());
		query.setParameter(0, value);
		return query.list();
	}

	public List<?> find(String queryString, Object[] values) {
		Query query = getSession().createQuery(queryString.toString());
		setParameter(query, values);
		return query.list();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T findSingle(final String hql, final Object... values) {
		Query query = getSession().createQuery(hql.toString());
		setParameter(query, values);
		List list = query.list();
		if (list != null && list.size() > 0) {
			return (T) list.get(0);
		} else {
			return null;
		}

	}

	// 使用带参数的HSQL语句增加、更新、删除实体
	public int bulkUpdate(String queryString, Object[] values) {
		Query query = getSession().createQuery(queryString);
		setParameter(query, values);
		int count = query.executeUpdate();
		// getSession().clear();
		return count;
	}

	public int executeHql(final String hql, int maxResults) {
		Query query = getSession().createQuery(hql);
		query.setMaxResults(maxResults);
		int i = query.executeUpdate();
		return i;
	}

	@SuppressWarnings("rawtypes")
	public List findWithPage(final String hql, final Map<String, Object> params, int startIndex, int size) {

		Query query = this.getSession().createQuery(hql);

		if (params != null) {
			Iterator<?> it = params.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				query.setParameter(key.toString(), params.get(key));
			}
		}
		query.setFirstResult(startIndex);
		query.setMaxResults(size);

		return query.list();

	}


	public List findByParamMap(final String queryString, final Map<String, Object> params) {
		Query query = this.getSession().createQuery(queryString);
		query.setProperties(params);
		return query.list();
	}

	/**
	 * 执行Hql语句返回字符串值
	 */
	public String getCommaHQL(String hql, Object[] values) {
		String result = null;
		List<?> list = find(hql, values);
		if (list != null && list.size() > 0) {
			if (list.get(0) != null) {
				result = list.get(0).toString();
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	protected Query setParameter(Query query, Map<String, Object> parameterMap) {
		if (parameterMap != null) {
			for (Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				query.setParameter(key, parameterMap.get(key));
			}
		}
		return query;
	}

	private Query setParameter(Query query, Object[] values) {
		int i = 0;
		if (values != null) {
			for (Object value : values) {
				query.setParameter(i, value);
				i++;
			}
		}

		return query;
	}

	/**
	 * 根据code，type获取uc字典表中的name
	 * 
	 * @param code
	 * @param type
	 * @return
	 */
	public String getIteamName(String code, String type) {
		String hql = "select t.iteamName from UcDictionaryConfig t where t.iteamCode = ? and t.typeCode =?";
		String iteamName = this.getCommaHQL(hql, new Object[] { code, type });
		return iteamName;
	}

	/**
	 * 根据code，type获取uc字典表中的name(特殊清苦查询)如拍拍贷接口中银行不对应
	 * 
	 * @param code
	 * @param type
	 * @return
	 */
	public String getIteamCode(String ppdBankCode, String type) {
		String hql = "select t.iteamCode from UcDictionaryConfig t where t.ppdBankCode = ? and t.typeCode =?";
		String iteamName = this.getCommaHQL(hql, new Object[] { ppdBankCode, type });
		return iteamName;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");

	public void update(Object o) {
		this.getSession().update(o);
	}
}