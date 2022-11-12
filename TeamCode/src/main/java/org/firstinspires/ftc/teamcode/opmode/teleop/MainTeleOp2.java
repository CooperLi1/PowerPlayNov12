package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Robot;
import org.firstinspires.ftc.teamcode.subsystem.lift.Lift;
import org.firstinspires.ftc.teamcode.subsystem.lift.LiftConstants;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class MainTeleOp2 extends LinearOpMode {


    private Robot robot;
    private double goal;
    private intakeState intakeState;
    private liftState liftState;
    private ElapsedTime timer;

    private GamepadEx gamepadEx2, gamepadEx1;

    private boolean lastAPress=false;
    private boolean lastBPress=false;

    private boolean mineralDetected=false;
    private boolean canTurn = false;
    private double multi = 1;

    public double OpenDelay = 500;
    public double CloseDelay = 1000;
    public double UpLiftMulti = 1;
    public double DownLiftMulti = 1;
    public double TurretMulti = 0.3;
    public double armpowermulti = 1;

    public enum intakeState {
        IDLE,
        INTAKING,
        GRABBED
    }

    public enum liftState {
        IDLE,
        INTAKE,
        LIFTED
    }


    public void runOpMode() throws InterruptedException {
        robot = new Robot(telemetry, hardwareMap);
        intakeState = intakeState.IDLE;
        liftState = liftState.IDLE;
        timer = new ElapsedTime();
        goal = 1.5;
        gamepadEx2 = new GamepadEx(gamepad2);
        gamepadEx1 = new GamepadEx(gamepad1);

        waitForStart();
        robot.init();
        liftState = liftState.IDLE;
        intakeState = intakeState.IDLE;

        while (!isStopRequested() && opModeIsActive()) {

            //anti-tip + regular teleop code - ONLY ON PITCH RIGHT NOW
            double pitch = robot.drive.getOrientation().firstAngle;


            double antiTipMulti = 0.5;
            robot.drive.setWeightedDrivePower(
                    new Pose2d(
                            gamepadEx1.getLeftY() * multi, //+ antiTipMulti * pitch
                            -gamepadEx1.getLeftX() * multi,
                            -gamepadEx1.getRightX() * multi
                    )
            );
            double Trigger1 = gamepadEx2.gamepad.left_trigger;
            double Trigger2 = -gamepadEx2.gamepad.right_trigger;

            //robot.lift.setPower(Trigger1 * UpLiftMulti);
            //robot.lift.setPower(Trigger2 * DownLiftMulti);

            double TurretControl = gamepadEx2.getLeftX();
            telemetry.addData("turret running?", TurretControl);
            int currentPosition = Math.round(robot.lift.turretmotor.getCurrentPosition());
            robot.lift.turretmotor.setTargetPosition(0);

            double armpower = gamepadEx2.getLeftY();
            //robot.lift.setArmPos(robot.lift.getArmPosition() - armpower * armpowermulti);

            if (gamepad2.dpad_up) {
                goal = 5;
            }
            if (gamepad2.dpad_right) {
                goal = 5;
            }
            if (gamepad2.dpad_left) {
                goal = 3;
            }

            switch (intakeState) {
                case IDLE:
                    canTurn = true;
                    robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                    robot.lift.setArmPos(LiftConstants.IdleArm);
                    robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
                            timer.reset();
                            canTurn = false;
                            intakeState = intakeState.INTAKING;
                        }
                    }
                    break;
                case INTAKING:
                    robot.lift.setArmPos(LiftConstants.IntakingHeight);
                    robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
                    if (robot.lift.getDistance() < 1.5) {
                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                        if (timer.milliseconds() > CloseDelay) {
                            intakeState = intakeState.GRABBED;
                            robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
                            robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
                            robot.lift.setArmPos(LiftConstants.IdleArm);
                            canTurn = true;
                            timer.reset();
                        }
                    }
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                            intakeState = intakeState.GRABBED;
                            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                            robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
                            canTurn = true;
                            timer.reset();
                        }
                    }
                    break;
                case GRABBED:
                    if (timer.milliseconds() > 300) {
                        robot.lift.setArmPos(LiftConstants.IdleArm);
                    }
                    if (timer.milliseconds() > 1500) {
                        if (gamepad1.left_bumper) {
                            timer.reset();
                            intakeState = intakeState.IDLE;
                        }
                    }
                    break;
            }

            switch (liftState) {
                case IDLE:
                    canTurn = true;
                    if (gamepad1.right_bumper) {
                        robot.lift.setTargetHeight(goal);
                        timer.reset();
                        liftState = liftState.LIFTED;
                    }
                    break;
                case LIFTED:
                    canTurn = true;
                    if (timer.milliseconds() > 750) {
                        if (gamepad1.right_bumper) {
                            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                            liftState = liftState.IDLE;
                        }
                    }
                    break;
            }

            if (canTurn == true) {
                if (gamepad2.y) {
                    robot.lift.turretmotor.setTargetPosition(LiftConstants.BackTurret);
                }
                if (gamepad2.b) {
                    robot.lift.turretmotor.setTargetPosition(LiftConstants.RightTurret);
                }
                if (gamepad2.x) {
                    robot.lift.turretmotor.setTargetPosition(LiftConstants.LeftTurret);
                }
                if (gamepad2.a) {
                    robot.lift.turretmotor.setTargetPosition(LiftConstants.FrontTurret);
                }
                if (gamepad2.left_bumper) {
                    robot.lift.turretmotor.setPower(0.1);
                }
                if (gamepad2.right_bumper) {
                    robot.lift.turretmotor.setPower(-0.1);
                }
            }

            gamepadEx2.readButtons();
            gamepadEx1.readButtons();

            telemetry.addData("intakeState", intakeState);
            telemetry.addData("liftState", liftState);
            telemetry.addData("height", robot.lift.getHeight());
            telemetry.addData("targetheight", robot.lift.getTargetHeight());
            telemetry.addData("intakemode", intakeState);
            telemetry.addData("liftmode", liftState);
            telemetry.addData("Distance", robot.lift.getDistance());
            telemetry.addData("Orientation", robot.drive.getOrientation());
            telemetry.addData("clawpos", robot.lift.clawServo.getPosition());
            telemetry.addData("clawpos2", robot.lift.clawServoB.getPosition());
            telemetry.addData("armpos", robot.lift.armServo1.getPosition());
            robot.update();
        }
    }
}
