package cn.zto.example.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "sys_job_info")
@DynamicInsert(value=true)
@DynamicUpdate(value=true)
public class SysJobInfo implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long sjiIdN;
	private String sjiCodeV;
	private String sjiSpringIdV;
	private String sjiMethodNameV;
	private String sjiCronV;
	private Integer sjiStatusN;
	private Timestamp inserttime;
	private Timestamp updatetime;
	private Short isactive;

	// Constructors

	/** default constructor */
	public SysJobInfo() {
	}

	/** full constructor */
	public SysJobInfo(String sjiCodeV, String sjiSpringIdV, String sjiMethodNameV, String sjiCronV, Integer sjiStatusN,
			Timestamp inserttime, Timestamp updatetime, Short isactive) {
		this.sjiCodeV = sjiCodeV;
		this.sjiSpringIdV = sjiSpringIdV;
		this.sjiMethodNameV = sjiMethodNameV;
		this.sjiCronV = sjiCronV;
		this.sjiStatusN = sjiStatusN;
		this.inserttime = inserttime;
		this.updatetime = updatetime;
		this.isactive = isactive;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "SJI_ID_N", unique = true, nullable = false)
	public Long getSjiIdN() {
		return this.sjiIdN;
	}

	public void setSjiIdN(Long sjiIdN) {
		this.sjiIdN = sjiIdN;
	}

	@Column(name = "SJI_CODE_V", unique = true, nullable = false, length = 20)
	public String getSjiCodeV() {
		return this.sjiCodeV;
	}

	public void setSjiCodeV(String sjiCodeV) {
		this.sjiCodeV = sjiCodeV;
	}

	@Column(name = "SJI_SPRING_ID_V", nullable = false, length = 50)
	public String getSjiSpringIdV() {
		return this.sjiSpringIdV;
	}

	public void setSjiSpringIdV(String sjiSpringIdV) {
		this.sjiSpringIdV = sjiSpringIdV;
	}

	@Column(name = "SJI_METHOD_NAME_V", nullable = false, length = 50)
	public String getSjiMethodNameV() {
		return this.sjiMethodNameV;
	}

	public void setSjiMethodNameV(String sjiMethodNameV) {
		this.sjiMethodNameV = sjiMethodNameV;
	}

	@Column(name = "SJI_CRON_V", nullable = false, length = 50)
	public String getSjiCronV() {
		return this.sjiCronV;
	}

	public void setSjiCronV(String sjiCronV) {
		this.sjiCronV = sjiCronV;
	}

	@Column(name = "SJI_STATUS_N", nullable = false)
	public Integer getSjiStatusN() {
		return this.sjiStatusN;
	}

	public void setSjiStatusN(Integer sjiStatusN) {
		this.sjiStatusN = sjiStatusN;
	}

	@Column(name = "inserttime", length = 19)
	public Timestamp getInserttime() {
		return this.inserttime;
	}

	public void setInserttime(Timestamp inserttime) {
		this.inserttime = inserttime;
	}

	@Column(name = "updatetime", length = 19)
	public Timestamp getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}

	@Column(name = "isactive")
	public Short getIsactive() {
		return this.isactive;
	}

	public void setIsactive(Short isactive) {
		this.isactive = isactive;
	}
}
