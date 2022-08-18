package de.craftery.writer.pom;

import de.craftery.writer.WritingSection;
import lombok.Getter;

public class PomProjectSection extends WritingSection {
    public PomProjectSection() {
        super(0);
    }

    @Getter
    private final PomRepositories repositories = new PomRepositories();

    @Getter
    private final PomDependencies dependencies = new PomDependencies();

    @Override
    public void buildLines() {
        this.getLines().add("""
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">""");
        this.getLines().add("    <modelVersion>4.0.0</modelVersion>");
        this.getLines().add("    <groupId>de.craftery</groupId>");
        this.getLines().add("    <artifactId>autogenerated</artifactId>");
        this.getLines().add("    <version>1.0.0</version>");
        this.getLines().add("    <packaging>jar</packaging>");
        this.getLines().add("    <name>Autogenerated</name>");
        this.getLines().add("    <description>This plugin got autogenerated and translated from Skript by https://github.com/Crafter-Y/SkriptLang-Compiler/</description>");
        this.getLines().add("    <properties>");
        this.getLines().add("        <java.version>17</java.version>");
        this.getLines().add("        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>");
        this.getLines().add("    </properties>");
        this.getLines().add("    <build>");
        this.getLines().add("        <plugins>");
        this.getLines().add("            <plugin>");
        this.getLines().add("                <groupId>org.apache.maven.plugins</groupId>");
        this.getLines().add("                <artifactId>maven-compiler-plugin</artifactId>");
        this.getLines().add("                <version>3.8.1</version>");
        this.getLines().add("                <configuration>");
        this.getLines().add("                    <source>${java.version}</source>");
        this.getLines().add("                    <target>${java.version}</target>");
        this.getLines().add("                </configuration>");
        this.getLines().add("            </plugin>");
        this.getLines().add("            <plugin>");
        this.getLines().add("                <groupId>org.apache.maven.plugins</groupId>");
        this.getLines().add("                <artifactId>maven-shade-plugin</artifactId>");
        this.getLines().add("                <version>3.2.4</version>");
        this.getLines().add("                <executions>");
        this.getLines().add("                    <execution>");
        this.getLines().add("                        <phase>package</phase>");
        this.getLines().add("                        <goals>");
        this.getLines().add("                            <goal>shade</goal>");
        this.getLines().add("                        </goals>");
        this.getLines().add("                        <configuration>");
        this.getLines().add("                            <createDependencyReducedPom>false</createDependencyReducedPom>");
        this.getLines().add("                            <createSourcesJar>false</createSourcesJar>");
        this.getLines().add("                            <createTestSourcesJar>false</createTestSourcesJar>");
        this.getLines().add("                            <filters>");
        this.getLines().add("                                <filter>");
        this.getLines().add("                                    <artifact>*:*</artifact>");
        this.getLines().add("                                    <excludes>");
        this.getLines().add("                                        <exclude>META-INF/*.SF</exclude>");
        this.getLines().add("                                        <exclude>META-INF/*.DSA</exclude>");
        this.getLines().add("                                        <exclude>META-INF/*.RSA</exclude>");
        this.getLines().add("                                        <exclude>META-INF/*.kotlin_module</exclude>");
        this.getLines().add("                                        <exclude>META-INF/*.txt</exclude>");
        this.getLines().add("                                        <exclude>META-INF/proguard/*</exclude>");
        this.getLines().add("                                        <exclude>META-INF/services/*</exclude>");
        this.getLines().add("                                        <exclude>META-INF/versions/9/*</exclude>");
        this.getLines().add("                                        <exclude>*License*</exclude>");
        this.getLines().add("                                        <exclude>*LICENSE*</exclude>");
        this.getLines().add("                                    </excludes>");
        this.getLines().add("                                </filter>");
        this.getLines().add("                            </filters>");
        this.getLines().add("                        </configuration>");
        this.getLines().add("                    </execution>");
        this.getLines().add("                </executions>");
        this.getLines().add("            </plugin>");
        this.getLines().add("        </plugins>");
        this.getLines().add("    </build>");
        this.getLines().addAll(this.repositories.getIndentedLines());
        this.getLines().addAll(this.dependencies.getIndentedLines());
        this.getLines().add("</project>");
    }
}
