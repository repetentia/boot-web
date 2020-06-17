package com.repetentia.support.reload;

import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.repetentia.support.log.Marker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReloadExecutor implements Runnable {
	private ApplicationContext ac;
	private String id;
	public ReloadExecutor(ApplicationContext ac, String id) {
		this.ac = ac;
		this.id = id;
	}
	public void run() {
		Map<String, ReLoadable> refreshables = ac.getBeansOfType(ReLoadable.class);
		Set<String> set = refreshables.keySet();
		for (String key:set) {
			ReLoadable refreshable = refreshables.get(key);
			if (this.id != null && this.id.equals(refreshable.id())) {
				log.info(Marker.DATA_RELOAD, "- about to reload - {}", this.id);
				refreshable.reload();
			}
		}
	}
}