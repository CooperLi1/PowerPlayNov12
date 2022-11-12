package org.firstinspires.ftc.teamcode.opmode.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.subsystem.Robot;
import org.firstinspires.ftc.teamcode.subsystem.SleeveDetector;
import org.firstinspires.ftc.teamcode.subsystem.lift.LiftConstants;

@Autonomous
public class ParkAuto extends LinearOpMode {
    Pose2d START_POSE = new Pose2d(36, -64, Math.PI / 2);
    Vector2d PRELOAD_POSE = new Vector2d(40, -52);
    Pose2d PRELOADPOSCHANGE1_POSE = new Pose2d(36, -52, Math.PI / 2);
    Pose2d PRELOADSTOP_POSE = new Pose2d(32, -8, Math.PI);
    Vector2d CONESTACK = new Vector2d(61, -12);
    Vector2d CYCLEMIDPOINT = new Vector2d(40, -12);
    Pose2d HIGHPOLE_POSE = new Pose2d(24, -8, Math.PI / 2);
    Pose2d PARKINGPREP_POSE = new Pose2d(36, -24, Math.PI / 2);
    Pose2d PARKING1_POSE = new Pose2d(10, -36, 0);
    Pose2d PARKING2_POSE = new Pose2d(35, -36, 0);
    Pose2d PARKING3_POSE = new Pose2d(62, -36, 0);
    // inches
    double[] CONESTACKSTEPHEIGHT = {1.38, 1.38, 1.38, 1.38};
    double[] CONESTACKINITHEIGHT = {1.38, 1.38, 1.38, 1.38};

    // TODO: no movement for lift in debugging
    int liftPosition = 3;

    Robot robot;

    // Parking
    Pose2d PARKINGPOSE;
    private SleeveDetector detector;
    SleeveDetection.Color parkingPos = SleeveDetection.Color.RED;

    public void runOpMode() {
        robot = new Robot(telemetry, hardwareMap);
        ElapsedTime timer = new ElapsedTime();
        detector.init(hardwareMap, telemetry);

        // TODO: What Sensors to Use?
//        frontSensor = hardwareMap.get(AnalogInput.class, "frontSensor");
//        rightSensor = hardwareMap.get(AnalogInput.class, "rightSensor");

        // Init
        robot.lift.setClaw1Pos(LiftConstants.CLAWCLOSEPOS1);
        if(timer.milliseconds() > 400) {
            robot.lift.setArmPos(LiftConstants.IdleArm);
            robot.lift.setTargetHeight(LiftConstants.IdleHeight);
        }

        TrajectorySequenceBuilder preloadCycle = robot.drive.trajectorySequenceBuilder(START_POSE)
                .splineTo(PRELOAD_POSE, Math.PI / 6)
                .waitSeconds(0.4)
                .addTemporalMarker(0.3, () -> {
                    robot.lift.setTargetHeight(33);
                    robot.lift.setClaw1Pos(LiftConstants.CLAWOPENPOS1);
                });

        robot.drive.setPoseEstimate(START_POSE);

        // Waiting for start
        while (!isStarted() && !isStopRequested()) {
            parkingPos = detector.getColor();
            if (parkingPos == SleeveDetection.Color.MAGENTA) {
                PARKINGPOSE = PARKING3_POSE;
            } else if ( parkingPos == SleeveDetection.Color.RED) {
                PARKINGPOSE = PARKING2_POSE;
            } else if (parkingPos == SleeveDetection.Color.BLUE) {
                PARKINGPOSE = PARKING1_POSE;
            }
            telemetry.addData("Parking position", parkingPos);
            telemetry.update();
        }
        detector.stop();

        //robot.drive.followTrajectorySequenceAsync(preloadCycle);
        while (!isStopRequested() && robot.drive.isBusy() && opModeIsActive()) {
            robot.update();
        }

        // Find out What "1.2" means
        for (int i = 0; i < 4; i++) {
            timer.reset();
            while (timer.milliseconds() < 500 && opModeIsActive()) {
                //relocalize();
                robot.drive.setDrivePower(new Pose2d(0.1, 0, 0));
                if (robot.lift.getDistance() < 1.2 && robot.drive.getPoseEstimate().getX() > 30) {
//                    robot.lift.setDoorPos(LiftConstants.DOOR_CLOSE_POS);
//                    robot.intake.setPower(-0.5);
                }
                robot.update();
            }

//            robot.drive.followTrajectorySequenceAsync(regularCycles[i]);

            while (!isStopRequested() && robot.drive.isBusy() && opModeIsActive()) {
                robot.update();
                if (robot.lift.getDistance() < 1.2 && robot.drive.getPoseEstimate().getX() > 30 && timer.milliseconds() > 2000) {
//                    robot.lift.setDoorPos(LiftConstants.DOOR_CLOSE_POS);
//                    robot.intake.setPower(-0.2);
                }
            }
        }
    }

//    public void relocalize() {
//        double rightDist = voltageToDistance(rightSensor.getVoltage());
//        double frontDist = frontSensor.getVoltage()/0.0032;
//        double heading = robot.drive.getPoseEstimate().getHeading();
//
//        if(rightDist > 30) {
//            rightDist = 0;
//        }
//
//        if(!(frontDist > 50 || frontDist < 5)) {
//            double yPos = -72 + rightDist * Math.cos(heading) - rightDistancePos.rotated(heading).getY();
//            double xPos = 72 - frontDist * Math.cos(heading) - frontDistancePos.rotated(heading).getX();
//            robot.drive.setPoseEstimate(new Pose2d(xPos, yPos, heading));
//        } else {
//            double yPos = -72 + rightDist * Math.cos(heading) - rightDistancePos.rotated(heading).getY();
//            double xPos = 72 - frontDist * Math.cos(heading) - frontDistancePos.rotated(heading).getX();
//            robot.drive.setPoseEstimate(new Pose2d(robot.drive.getPoseEstimate().getX(), yPos, heading));
//        }
//    }
//    public double voltageToDistance(double voltage) {return (voltage - 0.142) * 12 / 0.138;}
}