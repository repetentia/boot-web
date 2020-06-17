package com.repetentia.web.component.message;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface MessageMapper {
	@SelectProvider(type = MessageSqlProvider.class,  method = "findAll")
	public List<RtaMessageSource> findAll();
	@UpdateProvider(type = MessageSqlProvider.class,  method = "update")
	public int update(RtaMessageSource gripMessageSource);
	@InsertProvider(type = MessageSqlProvider.class,  method = "insert")
	public int insert(RtaMessageSource gripMessageSource);
}
