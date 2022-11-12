//package org.firstinspires.ftc.teamcode.opmode.teleop;
//
//import com.acmerobotics.roadrunner.geometry.Pose2d;
//import com.arcrobotics.ftclib.gamepad.GamepadEx;
//import com.arcrobotics.ftclib.gamepad.GamepadKeys;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.apache.commons.math3.genetics.ElitisticListPopulation;
//import org.firstinspires.ftc.teamcode.subsystem.Robot;
//import org.firstinspires.ftc.teamcode.subsystem.lift.LiftConstants;
//
//@com.qualcomm.robotcore.eventloop.opmode.TeleOp
//public class MainTeleOp extends LinearOpMode {
//
//
//    private Robot robot;
//    private ScoreMode mode;
//    private State state;
//    private ElapsedTime timer;
//
//    private GamepadEx gamepadEx2, gamepadEx1;
//
//    private boolean lastAPress=false;
//    private boolean lastBPress=false;
//
//    private boolean mineralDetected=false;
//    private double multi = 1;
//
//    public double OpenDelay = 500;
//    public double CloseDelay = 500;
//    public double UpLiftMulti = 1;
//    public double DownLiftMulti = 1;
//    public double TurretMulti = 0.3;
//    public double armpowermulti = 1;
//
//    public enum State {
//        IDLE,
//        Idle2,
//        IDLEBACK,
//        IDLEBACK2,
//        INTAKINGFront,
//        IntakingBack,
//        OuttakingBack,
//        OuttakingLeft,
//        OuttakingRight,
//        OuttakingFront,
//    }
//
//    public enum ScoreMode {
//        HIGH,
//        Ground,
//        LOW,
//        MID
//    }
//
//
//    public void runOpMode() throws InterruptedException {
//        robot = new Robot(telemetry, hardwareMap);
//        state = State.IDLE;
//        timer = new ElapsedTime();
//        mode = ScoreMode.HIGH;
//        gamepadEx2 = new GamepadEx(gamepad2);
//        gamepadEx1 = new GamepadEx(gamepad1);
//
//        waitForStart();
//        robot.init();
//        idleTransition();
//        while(!isStopRequested() && opModeIsActive()) {
//
//            //anti-tip + regular teleop code - ONLY ON PITCH RIGHT NOW
//            double pitch = robot.drive.getOrientation().firstAngle;
//
//
//            double antiTipMulti = 0.5;
//            robot.drive.setWeightedDrivePower(
//                    new Pose2d(
//                            gamepadEx1.getLeftY() * multi, //+ antiTipMulti * pitch
//                            -gamepadEx1.getLeftX() * multi,
//                            -gamepadEx1.getRightX() * multi
//                    )
//            );
//            double Trigger1 = gamepadEx2.gamepad.left_trigger;
//            double Trigger2 = -gamepadEx2.gamepad.right_trigger;
//
//            robot.lift.setPower(Trigger1*UpLiftMulti);
//            robot.lift.setPower(Trigger2*DownLiftMulti);
//
//            double TurretControl = gamepadEx2.getLeftX();
//            robot.lift.turretmotor.setPower(TurretControl*TurretMulti);
//
//            double armpower = gamepadEx2.getLeftY();
//            robot.lift.setArmPos(robot.lift.getArmPosition() - armpower*armpowermulti);
//
//            if(gamepad2.dpad_up) {
//                mode = ScoreMode.HIGH;
//            }
//            if(gamepad2.dpad_right) {
//                mode = ScoreMode.LOW;
//            }
//            if(gamepad2.dpad_left) {
//                mode = ScoreMode.MID;
//            }
//            if(gamepad2.dpad_down){
//                mode = ScoreMode.Ground;
//            }
//
//            switch(state) {
//                case IDLE:
//                    if (gamepad2.a) {
//                        state = State.INTAKINGFront;
//                        IntakingFrontTransition();
//                    }
//                    if (gamepad2.y) {
//                        state = State.IDLEBACK;
//                        IdleBackTransition();
//                    }
//                    break;
//                case INTAKINGFront:
//                    if (robot.lift.getDistance() < 5) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWCLOSEPOS2);
//                        if (timer.milliseconds() > CloseDelay) {
//                            state = State.Idle2;
//                            Idle2Transition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.a) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWCLOSEPOS2);
//                        if (timer.milliseconds() > CloseDelay){
//                            state = State.Idle2;
//                            Idle2Transition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.start) {
//                        state = State.IDLE;
//                        idleTransition();
//                    }
//                    break;
//                case Idle2:
//                    if (gamepad2.a) {
//                        state = State.OuttakingFront;
//                        OuttakingFrontTransition();
//                    }
//                    if (gamepad2.b) {
//                        state = State.OuttakingRight;
//                        OuttakingRightTransition();
//                    }
//                    if (gamepad2.x) {
//                        state = State.OuttakingLeft;
//                        OuttakingLeftTransition();
//                    }
//                    if (gamepad2.y) {
//                        state = State.OuttakingBack;
//                        OuttakingBackTransition();
//                    }
//                    if (gamepad2.start) {
//                        state = State.INTAKINGFront;
//                        IntakingFrontTransition();
//                    }
//                    break;
//                case IDLEBACK:
//                    if (gamepad2.y) {
//                        state = State.IntakingBack;
//                        IntakingBackTransition();
//                    }
//                    if (gamepad2.a) {
//                        state = State.IDLE;
//                        idleTransition();
//                    }
//                    break;
//                case IntakingBack:
//                    if (robot.lift.getDistance() < 5) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWCLOSEPOS2);
//                        if (timer.milliseconds() > CloseDelay) {
//                            state = State.IDLEBACK2;
//                            IdleBack2Transition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.y) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWCLOSEPOS2);
//                        if (timer.milliseconds() > CloseDelay) {
//                            state = State.IDLEBACK2;
//                            IdleBack2Transition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.start) {
//                        state = State.IDLEBACK;
//                        IdleBackTransition();
//                    }
//                    break;
//                case IDLEBACK2:
//                    if (gamepad2.a) {
//                        state = State.OuttakingFront;
//                        OuttakingFrontTransition();
//                    }
//                    if (gamepad2.b) {
//                        state = State.OuttakingRight;
//                        OuttakingRightTransition();
//                    }
//                    if (gamepad2.x) {
//                        state = State.OuttakingLeft;
//                        OuttakingLeftTransition();
//                    }
//                    if (gamepad2.y) {
//                        state = State.OuttakingBack;
//                        OuttakingBackTransition();
//                    }
//                    if (gamepad2.start) {
//                        state = State.IntakingBack;
//                        IntakingBackTransition();
//                    }
//                    break;
//                case OuttakingFront:
//                    if (gamepad2.a) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay){
//                            state = State.IDLE;
//                            idleTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.back) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay) {
//                            state = State.IDLEBACK;
//                            IdleBackTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.y){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                    }
//                    if (gamepad2.b){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                    }
//                    if (gamepad2.x){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_left){
//                        robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                    }
//                    if (gamepad2.dpad_right){
//                        robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                    }
//                    if (gamepad2.dpad_down){
//                        robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                    }
//                    if (gamepad2.start) {
//                        state = State.Idle2;
//                        idleTransition();
//                    }
//                    break;
//                case OuttakingBack:
//                    if (gamepad2.y) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay){
//                            state = State.IDLE;
//                            idleTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.back) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay) {
//                            state = State.IDLEBACK;
//                            IdleBackTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.a){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                    }
//                    if (gamepad2.b){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                    }
//                    if (gamepad2.x){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_left){
//                        robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                    }
//                    if (gamepad2.dpad_right){
//                        robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                    }
//                    if (gamepad2.dpad_down){
//                        robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                    }
//                    if (gamepad2.start) {
//                        state = State.Idle2;
//                        idleTransition();
//                    }
//                    break;
//                case OuttakingLeft:
//                    if (gamepad2.x) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay){
//                            state = State.IDLE;
//                            idleTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.back) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay) {
//                            state = State.IDLEBACK;
//                            IdleBackTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.a){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                    }
//                    if (gamepad2.b){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                    }
//                    if (gamepad2.y){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_left){
//                        robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                    }
//                    if (gamepad2.dpad_right){
//                        robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                    }
//                    if (gamepad2.dpad_down){
//                        robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                    }
//                    if (gamepad2.start) {
//                        state = State.Idle2;
//                        idleTransition();
//                    }
//                    break;
//                case OuttakingRight:
//                    if (gamepad2.b) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay){
//                            state = State.IDLE;
//                            idleTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.back) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
//                        robot.lift.setClaw2Pos(LiftConstants.CLAWOPENPOS2);
//                        if (timer.milliseconds() > OpenDelay) {
//                            state = State.IDLEBACK;
//                            IdleBackTransition();
//                            timer.reset();
//                        }
//                    }
//                    if (gamepad2.a){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                    }
//                    if (gamepad2.y){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                    }
//                    if (gamepad2.x){
//                        robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_up){
//                        robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                    }
//                    if (gamepad2.dpad_left){
//                        robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                    }
//                    if (gamepad2.dpad_right){
//                        robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                    }
//                    if (gamepad2.dpad_down){
//                        robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                    }
//                    if (gamepad2.start) {
//                        state = State.Idle2;
//                        idleTransition();
//                    }
//                    break;
//            }
//
//            }
//
//
//            gamepadEx2.readButtons();
//            gamepadEx1.readButtons();
//
//            addDebugInfo();
//            robot.update();
//        }
//
//
//    public void addDebugInfo() {
//        telemetry.addData("State", state);
//        telemetry.addData("ScoreMode", mode);
//        telemetry.addData("Distance", robot.lift.getDistance());
//        telemetry.addData("Orientation", robot.drive.getOrientation());;
////        telemetry.addData("Gamepad2 left stick y", gamepadEx2.getLeftY());
////        telemetry.addData("Gamepad2 left stick x", gamepadEx2.getLeftX());
////        telemetry.addData("Gamepad2 right stick x", gamepadEx2.getRightX());
//    }
//
//    public void idleTransition() {
//        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//        robot.lift.setArmPos(LiftConstants.IdleArm);
//    }
//
//    public void IdleBackTransition() {
//        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//        robot.lift.setArmPos(LiftConstants.IdleArm);
//    }
//
//    public void IntakingFrontTransition() {
//        robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//        robot.lift.setArmPos(LiftConstants.IntakingArm);
//    }
//
//    public void IntakingBackTransition() {
//        robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//        robot.lift.setArmPos(LiftConstants.IntakingArm);
//    }
//
//    public void Idle2Transition() {
//        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//        robot.lift.setArmPos(LiftConstants.IdleArm);
//    }
//
//    public void IdleBack2Transition() {
//        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
//        robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//        robot.lift.setArmPos(LiftConstants.IdleArm);
//    }
//
//    public void OuttakingFrontTransition() {
//        switch(mode) {
//            case Ground:
//                robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                robot.lift.setArmPos(LiftConstants.GroundArm);
//                break;
//            case LOW:
//                robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                robot.lift.setArmPos(LiftConstants.LowArm);
//                break;
//            case MID:
//                robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                robot.lift.setArmPos(LiftConstants.MidArm);
//                break;
//            case HIGH:
//                robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
//                robot.lift.setArmPos(LiftConstants.HighArm);
//                break;
//        }
//    }
//    public void OuttakingBackTransition() {
//        switch(mode) {
//            case Ground:
//                robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                robot.lift.setArmPos(LiftConstants.GroundArm);
//                break;
//            case LOW:
//                robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                robot.lift.setArmPos(LiftConstants.LowArm);
//                break;
//            case MID:
//                robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                robot.lift.setArmPos(LiftConstants.MidArm);
//                break;
//            case HIGH:
//                robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
//                robot.lift.setArmPos(LiftConstants.HighArm);
//                break;
//        }
//    }
//    public void OuttakingLeftTransition() {
//        switch(mode) {
//            case Ground:
//                robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                robot.lift.setArmPos(LiftConstants.GroundArm);
//                break;
//            case LOW:
//                robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                robot.lift.setArmPos(LiftConstants.LowArm);
//                break;
//            case MID:
//                robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                robot.lift.setArmPos(LiftConstants.MidArm);
//                break;
//            case HIGH:
//                robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
//                robot.lift.setArmPos(LiftConstants.HighArm);
//                break;
//        }
//    }
//    public void OuttakingRightTransition() {
//        switch(mode) {
//            case Ground:
//                robot.lift.setTargetHeight(LiftConstants.GROUNDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                robot.lift.setArmPos(LiftConstants.GroundArm);
//                break;
//            case LOW:
//                robot.lift.setTargetHeight(LiftConstants.LOWJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                robot.lift.setArmPos(LiftConstants.LowArm);
//                break;
//            case MID:
//                robot.lift.setTargetHeight(LiftConstants.MIDJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                robot.lift.setArmPos(LiftConstants.MidArm);
//                break;
//            case HIGH:
//                robot.lift.setTargetHeight(LiftConstants.HIGHJUNCTION);
//                robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
//                robot.lift.setArmPos(LiftConstants.HighArm);
//                break;
//        }
//    }
//}
