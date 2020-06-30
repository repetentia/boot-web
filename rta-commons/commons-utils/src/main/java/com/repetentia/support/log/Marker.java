package com.repetentia.support.log;

import org.slf4j.MarkerFactory;

public interface Marker {
	org.slf4j.Marker ENV = MarkerFactory.getMarker("ENV");
	org.slf4j.Marker SCAN = MarkerFactory.getMarker("SCAN");
	org.slf4j.Marker DATA_LOAD = MarkerFactory.getMarker("DATA-LOAD");
	org.slf4j.Marker DATA_RELOAD = MarkerFactory.getMarker("DATA-RELOAD");
	org.slf4j.Marker MESSAGE = MarkerFactory.getMarker("MESSAGE");
	org.slf4j.Marker BEAN_INIT = MarkerFactory.getMarker("BEAN-INIT");
	org.slf4j.Marker AFTER_PROP = MarkerFactory.getMarker("AFTER-PROP");
	org.slf4j.Marker DB_CONFIG = MarkerFactory.getMarker("DB-CONFIG");
	org.slf4j.Marker BOOT_CONFIG = MarkerFactory.getMarker("BOOT-CONFIG");
	org.slf4j.Marker VIEW = MarkerFactory.getMarker("VIEW");
	org.slf4j.Marker PAGE = MarkerFactory.getMarker("PAGE");
	org.slf4j.Marker REST_GET = MarkerFactory.getMarker("REST-GET");
	org.slf4j.Marker REST_POST= MarkerFactory.getMarker("REST-POST");
	org.slf4j.Marker POST = MarkerFactory.getMarker("POST");
	org.slf4j.Marker SQL = MarkerFactory.getMarker("SQL");
}
