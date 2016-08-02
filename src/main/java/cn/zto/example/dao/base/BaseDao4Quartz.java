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

	// ���η�ҳ��ѯ
	public List findWithPage(String queryString, Map<String, Object> params, int startIndex, int size);

	public List findByParamMap(String queryString, Map<String, Object> params);

	// ִ��Hql��䷵���ַ���ֵ
	public String getCommaHQL(String hql, Object[] values);
	/**
	 * ������� group by ��֧�� ���� group by �ķ�ҳ
	 * @param namedParamHql
	 * @param results
	 */
	public int executeHql(String hql, int batchRows);

	String getIteamName(String code, String type);

	String getIteamCode(String ppdBankCode, String type);
	
	void update(Object o);
}
