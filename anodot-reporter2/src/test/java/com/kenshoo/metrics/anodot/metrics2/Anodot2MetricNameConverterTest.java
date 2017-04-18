package com.kenshoo.metrics.anodot.metrics2;

import com.kenshoo.metrics.anodot.AnodotProperties;
import com.kenshoo.metrics.anodot.AnodotGlobalProperties;
import com.yammer.metrics.core.spec.MetricName;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2MetricNameConverterTest {

    private final static String VERSION = "V33";
    private final static String BUILD = "1234";

    private Anodot2MetricNameConverter nameConverter = new Anodot2MetricNameConverter(new AnodotGlobalProperties(VERSION, BUILD));

    @Test
    public void commonCaseParsedProperly() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.MyClass.metricNameWith_Underscore.subname");
        assertProperty(anodotName, AnodotProperties.CLASS_PROPERTY, "MyClass");
        assertProperty(anodotName, AnodotProperties.PACKAGE_PROPERTY, "com_kenshoo");
        assertProperty(anodotName, AnodotProperties.WHAT_PROPERTY, "metricNameWith_Underscore_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void noUpperCasePartsMeanNoPackageAndClass() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.package.metric.subname");
        assertProperty(anodotName, AnodotProperties.CLASS_PROPERTY, null);
        assertProperty(anodotName, AnodotProperties.PACKAGE_PROPERTY, null);
        assertProperty(anodotName, AnodotProperties.WHAT_PROPERTY, "com_kenshoo_package_metric_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void uppercasePackageNameMistakenForClassWhatCanYouDo() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.BadlyNamedPackage.MyClass.metricName.subname");
        assertProperty(anodotName, AnodotProperties.CLASS_PROPERTY, "BadlyNamedPackage");
        assertProperty(anodotName, AnodotProperties.PACKAGE_PROPERTY, "com_kenshoo");
        assertProperty(anodotName, AnodotProperties.WHAT_PROPERTY, "MyClass_metricName_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void noPackageMetricNameFallsBackToUseEntireNameAsWhat() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("MyClass.metricName.subname");
        assertProperty(anodotName, AnodotProperties.CLASS_PROPERTY, null);
        assertProperty(anodotName, AnodotProperties.PACKAGE_PROPERTY, null);
        assertProperty(anodotName, AnodotProperties.WHAT_PROPERTY, "MyClass_metricName_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void digitInPackageNameAllowed() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("org.apache.log4j.Appender.warn");
        assertProperty(anodotName, AnodotProperties.CLASS_PROPERTY, "Appender");
        assertProperty(anodotName, AnodotProperties.PACKAGE_PROPERTY, "org_apache_log4j");
        assertProperty(anodotName, AnodotProperties.WHAT_PROPERTY, "warn");
        assertCommonProperties(anodotName);
    }

    private void assertProperty(MetricName anodotName, String name, String value) {
        if (value == null) {
            assertThat(anodotName.getProperty(name), nullValue());
        } else {
            assertThat(anodotName.getProperty(name).getValue(), is(value));
        }
    }

    private void assertCommonProperties(MetricName anodotName) {
        assertProperty(anodotName, AnodotProperties.VERSION_PROPERTY, VERSION);
        assertProperty(anodotName, AnodotProperties.BUILD_PROPERTY, "1234");
        assertThat(anodotName.getProperty("server"), notNullValue());
    }

}