package com.elder.shopguide;

public class GuideSession {
    private GuideStep[] steps;
    private int currentIndex = 0;
    private String scene;
    private String sceneName;

    public GuideSession(String scene) {
        this.scene = scene;
        this.steps = GuideStep.getSteps(scene);
        GuideStep.SceneInfo info = GuideStep.getSceneInfo(scene);
        this.sceneName = (info != null) ? info.name : "\u672A\u77E5";
    }

    public GuideStep getCurrentStep() {
        return (steps != null && currentIndex < steps.length) ? steps[currentIndex] : null;
    }

    public int getCurrentIndex() { return currentIndex; }
    public int getTotalSteps() { return steps != null ? steps.length : 0; }
    public String getSceneName() { return sceneName; }
    public boolean isLastStep() { return steps != null && currentIndex >= steps.length - 1; }
    public void nextStep() { if (steps != null && currentIndex < steps.length - 1) currentIndex++; }
    public void prevStep() { if (currentIndex > 0) currentIndex--; }
}
