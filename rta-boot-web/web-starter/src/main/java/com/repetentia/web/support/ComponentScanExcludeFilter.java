package com.repetentia.web.support;

import java.io.IOException;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import com.repetentia.support.log.Marker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComponentScanExcludeFilter implements TypeFilter {

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
        String fullyQualifiedName = classMetadata.getClassName();
        String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
        boolean condition = false;
        log.info(Marker.SCAN, "{} {} - {}", fullyQualifiedName, result(condition), className);
        return condition;
	}
	private String result(boolean condition) {
		if (condition) return "is excluded!!!";
		return "is included!!!";
	}

}
