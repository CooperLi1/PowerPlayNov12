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
public class TwoManCarry extends LinearOpMode {

    private Robot robot;
    private double goal;
    private robotState robotState;
    private ElapsedTime timer;
    private boolean canTurn = false;
    double turretspeed = 0.3;
    double dtspeed = 1;


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
        goal = 32;
        waitForStart();
        robot.init();
        robotState = robotState.IDLE;
        //robot.lift.turretmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lift.turretmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        while (!isStopRequested() && opModeIsActive()) {

            //robot.lift.turretmotor.setTargetRotation(50);

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


//            int currentPosition = Math.round(robot.lift.turretmotor.getCurrentPosition());
//            telemetry.addData("turret running?", currentPosition);
//            telemetry.addData("turret running?", gamepad2.left_stick_x);
//            if (gamepad2.left_stick_x < -0.5) {
//                robot.lift.turretmotor.setTargetPosition(currentPosition - 25);
//            } else if (gamepad2.left_stick_x > 0.5) {
//                robot.lift.turretmotor.setTargetPosition(currentPosition + 25);
//            }

            if (gamepad2.a && turretspeed == 0.3) {
                if (timer.milliseconds() > 200) {
                    turretspeed = 0.6;
                }
            }else if(gamepad2.a && turretspeed == 0.6){
                if (timer.milliseconds() > 200) {
                    turretspeed = 0.3;
                }
            }

            if (gamepad1.a && dtspeed == 1) {
                if (timer.milliseconds() > 200) {
                    dtspeed = 0.4;
                }
            }else if(gamepad1.a && dtspeed == 0.4) {
                if (timer.milliseconds() > 200) {
                    dtspeed = 1;
                }
            }

            double TurretPower = gamepad2.left_stick_x;
            robot.lift.turretmotor.setPower(TurretPower * turretspeed);

            //robot.lift.setPower(Trigger1 * UpLiftMulti);
            //robot.lift.setPower(Trigger2 * DownLiftMulti)
            //robot.lift.setArmPos(robot.lift.getArmPosition() - armpower * armpowermulti);

            if (gamepad2.dpad_up) {
                goal = 32;
            }
            if (gamepad2.dpad_right) {
                goal = 22;
            }
            if (gamepad2.dpad_left) {
                goal = 10.5;
            }

            switch (robotState) {
                case IDLE:
                    canTurn = true;
                    robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
                    robot.lift.setArmPos(LiftConstants.IdleArm);
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.right_bumper) {
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
//                    if (robot.lift.getDistance() < 1.5) {
//                        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
//                        if (timer.milliseconds() > 750) {
//                            intakeState = intakeState.GRABBED;
//                            robot.lift.setArmPos(LiftConstants.IdleArm);
//                            timer.reset();
//                        }
//                    }

                    if (timer.milliseconds() > 500) {
                        if (gamepad1.left_bumper) {
                            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                            robotState = robotState.IDLE;
                            timer.reset();
                        }
                        if (gamepad1.right_bumper) {
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
                    }
                    if (timer.milliseconds() > 500) {
                        if (gamepad1.right_bumper) {
                            robot.lift.setTargetHeight(goal);
                            timer.reset();
                            robotState = robotState.LIFTED;
                        }
                    }
                    break;
                case LIFTED:
                    canTurn = true;
                    if (timer.milliseconds() > 750) {
                        if (gamepad1.left_bumper) {
                            timer.reset();
                            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
                            robotState = robotState.GRABBED;
                        }
                        if (gamepad1.right_bumper) {
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
            telemetry.addData("Height",robot.lift.getHeight());
            telemetry.addData("goal", goal);
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