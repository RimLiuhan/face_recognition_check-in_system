package com.lh.face_checkin_be.config.face_engine;

import com.arcsoft.face.FaceEngine;
import org.springframework.stereotype.Component;

/**
 * ClassName:Engine
 * Package:com.lh.face_checkin_be.pojo
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/4-20:20
 * @version:v1.0
 */
public class EngineInfo {
    private Integer errorCode;
    private FaceEngine faceEngine;
    public EngineInfo(Integer errorCode, FaceEngine faceEngine) {
        this.errorCode = errorCode;
        this.faceEngine = faceEngine;
    }
    public Integer getErrorCode() {
        return errorCode;
    }
    public FaceEngine getFaceEngine() {
        return faceEngine;
    }
}
