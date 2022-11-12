package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.ejml.dense.row.linsol.LinearSolver_DDRB_to_DDRM;
import org.firstinspires.ftc.teamcode.subsystem.Robot;
import org.firstinspires.ftc.teamcode.subsystem.lift.LiftConstants;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class CooperTele extends LinearOpMode {

    private Robot robot;
    private robotState robotState;
    private ElapsedTime timer;
    private boolean canTurn = false;
    double turretspeed = 0.4;
    double turretaddition = 40;
    double dtspeed = 1;
    double up = 31.5;
    double mid = 22;
    double low = 10.5;


    public double CloseDelay = 1000;

    public enum robotState {
        IDLE,
        INTAKING,
        GRABBED,
        LIFTED,
        DROPPED
    }


    public void runOpMode() throws InterruptedException {
        robot = new Robot(telemetry, hardwareMap);
        timer = new ElapsedTime();
        waitForStart();
        robot.init();
        robotState = robotState.IDLE;
        robot.lift.turretmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        while (!isStopRequested() && opModeIsActive()) {

            //anti-tip + regular teleop code - ONLY ON PITCH RIGHT NOW
            double pitch = robot.drive.getOrientation().firstAngle;
            double antiTipMulti = 0.5;
            robot.drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_x * dtspeed, //controls strafing
                            gamepad1.right_stick_x * dtspeed * 0.6, //controls turning
                            -gamepad1.left_stick_y * dtspeed //controls forward
                    )
            );

            if (gamepad2.right_bumper == true) {
                    turretaddition = 5;
            } else {
                    turretaddition = 40;
            }

            if (gamepad1.right_bumper = true) {
                    dtspeed = 0.4;
            } else{
                    dtspeed = 1;
            }

            double TurretPower = gamepad2.right_stick_x;
            if (canTurn = true) {
                if(TurretPower>0.5){
                    robot.lift.setTargetRotation(robot.lift.getCurrentRotation()+turretaddition);
                }else if(TurretPower<-0.5){
                    robot.lift.setTargetRotation(robot.lift.getCurrentRotation()-turretaddition);
                }

            }

            //robot.lift.setPower(Trigger1 * UpLiftMulti);
            //robot.lift.setPower(Trigger2 * DownLiftMulti)
            //robot.lift.setArmPos(robot.lift.getArmPosition() - armpower * armpowermulti);

            switch (robotState) {
                case IDLE:
                    canTurn = true;
                    robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                    robot.lift.setArmPos(LiftConstants.IdleArm);
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
                            timer.reset();
                            robotState = robotState.INTAKING;
                        }
                    }
                    break;
                case INTAKING:
                    canTurn = false;
                    robot.lift.setArmPos(LiftConstants.IntakingArm);
                    robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
                    robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                            robotState = robotState.GRABBED;
                            timer.reset();
                        }
                    }
                    break;
                case GRABBED:
                    canTurn = true;
                    if (timer.milliseconds() > 300) {
                        robot.lift.setArmPos(LiftConstants.IdleArm);
                        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                    }
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setTargetHeight(LiftConstants.IntakingHeight);
                            timer.reset();
                            robotState = robotState.INTAKING;
                        }
                        if (gamepad2.dpad_up) {
                            robot.lift.setTargetHeight(up);
                            timer.reset();
                            robotState = robotState.LIFTED;
                        }
                        if (gamepad2.dpad_right) {
                            robot.lift.setTargetHeight(mid);
                            timer.reset();
                            robotState = robotState.LIFTED;
                        }
                        if (gamepad2.dpad_left) {
                            robot.lift.setTargetHeight(low);
                            timer.reset();
                            robotState = robotState.LIFTED;
                        }
                    }
                    break;
                case LIFTED:
                    canTurn = true;
                    if (timer.milliseconds() > 750) {
                        if (gamepad2.dpad_down || gamepad1.dpad_down) {
                            timer.reset();
                            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                            robotState = robotState.GRABBED;
                        }
                        if (gamepad2.left_bumper || gamepad1.left_bumper) {
                            timer.reset();
                            robotState = robotState.DROPPED;
                        }
                    }
                    break;
                case DROPPED:
                    canTurn = false;
                    robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
                    if (timer.milliseconds() > 1000) {
                        robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                        robotState = robotState.IDLE;
                    }
                    break;

            }
            telemetry.addData("turret pos", robot.lift.getCurrentRotation());
            telemetry.addData("turret goal", robot.lift.getTargetRotation());
            telemetry.addData("State", robotState);
            telemetry.addData("Height", robot.lift.getHeight());
            telemetry.addData("Distance", robot.lift.getDistance());
            telemetry.addData("Orientation", robot.drive.getOrientation());
            telemetry.addData("armpos1", robot.lift.armServo1.getPosition());
            telemetry.addData("armpos2", robot.lift.armServo2.getPosition());
            telemetry.addData("timer", timer.milliseconds());
            telemetry.addData("dtspeed", dtspeed);
            robot.update();
        }
    }
}
