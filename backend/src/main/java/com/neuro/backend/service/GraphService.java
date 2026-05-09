package com.neuro.backend.service;

import java.util.Map;

public interface GraphService {
    Map<String, Object> getGlobalGraph(Long folderId);
}
