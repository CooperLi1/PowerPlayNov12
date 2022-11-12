package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.firstinspires.ftc.teamcode.subsystem.Robot;
import org.firstinspires.ftc.teamcode.subsystem.lift.LiftConstants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystem.Robot;
@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class ArmTuning extends LinearOpMode{
    private Robot robot;

    public void runOpMode(){
        robot = new Robot(telemetry, hardwareMap);
        waitForStart();
        telemetry.addData("loop:", "started");
        while(!isStopRequested()) {
            telemetry.addData("turret goal", robot.lift.turretmotor.getTargetPosition());
            telemetry.addData("turret pos", robot.lift.turretmotor.getCurrentPosition());
            robot.lift.turretmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.lift.turretmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.lift.turretmotor.setTargetPosition(100);
        }
    }

}

