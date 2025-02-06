package com.lh.face_checkin_be.config.face_engine;

import com.arcsoft.face.FaceEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName:EngineInfo
 * Package:com.lh.face_checkin_be.config.face_engine
 * Description:
 *
 * @author:LH寒酥
 * @create:2025/2/6-13:12
 * @version:v1.0
 */
public class EngineInfo {
    private Integer errorCode;
    private FaceEngine faceEngine;

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public FaceEngine getFaceEngine() {
        return faceEngine;
    }

    public void setFaceEngine(FaceEngine faceEngine) {
        this.faceEngine = faceEngine;
    }
}
