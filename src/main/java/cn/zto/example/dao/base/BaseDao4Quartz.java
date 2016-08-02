package cn.zto.example.dao.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao4Quartz {
	void flush();

	void clear();
	
	<T> void mergModify(T entity);
	
	public Object getObject(Class clazz, Serializable id);
	public void saveObject(Object o);

	long create(Object o);

	public void patchSave(List list);
	/**
	 * @Description Generic method to get objects by hql
	 * @param hqlString
	 *            : hql
	 * @return List
	 */
	public List<?> find(String hqlString);

	public List<?> find(String queryString, Object value);

	public List<?> find(String queryString, Object[] values);

	public <T> T findSingle(String hql, Object... values);

	public int bulkUpdate(String queryString, Object[] values);

	// 带参分页查询
	public List findWithPage(String queryString, Map<String, Object> params, int startIndex, int size);

	public List findByParamMap(String queryString, Map<String, Object> params);

	// 执行Hql语句返回字符串值
	public String getCommaHQL(String hql, Object[] values);
	/**
	 * 如果带有 group by 不支持 带有 group by 的分页
	 * @param namedParamHql
	 * @param results
	 */
	public int executeHql(String hql, int batchRows);

	String getIteamName(String code, String type);

	String getIteamCode(String ppdBankCode, String type);
	
	void update(Object o);
}
